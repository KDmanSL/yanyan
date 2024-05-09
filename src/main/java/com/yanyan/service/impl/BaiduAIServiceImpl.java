package com.yanyan.service.impl;

import cn.hutool.core.lang.UUID;

import com.baidu.aip.ocr.AipOcr;
import com.yanyan.dto.Result;
import com.yanyan.service.BaiduAIService;
import com.yanyan.utils.SystemConstants;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.yanyan.utils.SystemConstants.MQ_NAME_SERVER;

@Slf4j
@Service
public class BaiduAIServiceImpl implements BaiduAIService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ConcurrentHashMap<String, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("mqadd.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public String actionOcr(MultipartFile multipartFile) {
        Long userId = UserHolder.getUser().getId();
        String uuid = userId +":"+System.currentTimeMillis();
        if (multipartFile == null) {
            return null;
        }
        // 处理图片保存到本地Resources-images路径

        String fileUrl = null;
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String storedFileName = UUID.randomUUID().toString(true) + fileExtension;
            Path destinationFile = Paths.get("src/main/resources/static/images", storedFileName).toAbsolutePath().normalize();

            multipartFile.transferTo(destinationFile);

            fileUrl = destinationFile.toString();
        } catch (IOException e) {
            log.error("文件存取异常，{}", e.getMessage());
        }

//        Map<String, String> map = new HashMap<>();
//        map.put("uuid", uuid);
//        map.put("file_url", fileUrl);

//        stringRedisTemplate.opsForStream().add(MQ_NAME_SERVER, map);
        // 1.执行lua脚本
        stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                uuid, fileUrl
        );
        log.info("MQ添加成功", uuid);
        CompletableFuture<String> future = new CompletableFuture<>();
        futures.put(uuid, future);


        // 等待处理结果，最多等待一定时间
        try {
            String result = future.get(1, TimeUnit.MINUTES);
            return result;
        } catch (Exception e) {
            log.error("等待结果时发生错误：{}", e.getMessage());
            return null;
        } finally {
            futures.remove(uuid); // 清理
        }
    }

    private String imgHandlerAI(String imgurl) {
        // 处理图片比特流，调用百度AI,识别图片，返回图片信息
        AipOcr client = new AipOcr(SystemConstants.APP_ID
                , SystemConstants.API_KEY, SystemConstants.SECRET_KEY);
        HashMap<String, String> options = new HashMap<String, String>(4);
        options.put("detect_direction", "true");
        options.put("detect_language", "false");
        options.put("probability", "false");


        JSONObject res = client.basicGeneral(imgurl, options);
        String jsonData = "";
        try {
            jsonData = res.toString(2);
        } catch (JSONException e) {
            log.error("获取json数据异常，{}", e.getMessage());
        }
        return jsonData;
    }


    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
        EXECUTOR.submit(new MQHandler());
    }

    private class MQHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // 从消息队列获取消息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(MQ_NAME_SERVER, ReadOffset.lastConsumed())
                    );
                    if (list == null || list.isEmpty()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            log.error("线程休眠异常，{}", e.getMessage());
                        }
                        continue;
                    }
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    // 获取消息
                    String img_url = (String) value.get("file_url");
                    String uuid = (String) value.get("uuid");
                    // 将img_url调用
                    String img_info = imgHandlerAI(img_url);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("线程休眠异常，{}", e.getMessage());
                    }
                    // 确认消息ACK
                    stringRedisTemplate.opsForStream().acknowledge(MQ_NAME_SERVER, "g1", record.getId().getValue());

                    // 解决对应的Future
                    CompletableFuture<String> future = futures.get(uuid);
                    if (future != null) {
                        future.complete(img_info);
                    }


                } catch (Exception e) {
                    log.error("消息处理异常，{}", e.getMessage());
                    // 当出现异常时，重新尝试处理pending-list中的消息
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            while (true) {
                try {
                    // 从消息队列获取消息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(MQ_NAME_SERVER, ReadOffset.from("0"))
                    );
                    if (list == null || list.isEmpty()) {
                        // 说明异常消息已解决
                        break;
                    }
                    for (MapRecord<String, Object, Object> record : list) {
                        Map<Object, Object> value = record.getValue();
                        // 获取消息
                        String img_url = (String) value.get("img_url");
                        String uuid = (String) value.get("uuid");
                        // 将img_bit调用
                        String img_info = imgHandlerAI(img_url);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            log.error("线程休眠异常，{}", e.getMessage());
                        }
                        // 解决对应的Future
                        CompletableFuture<String> future = futures.get(uuid);
                        if (future != null) {
                            future.complete(img_info);
                        }

                        // 确认消息ACK
                        stringRedisTemplate.opsForStream().acknowledge(MQ_NAME_SERVER, "g1", record.getId().getValue());
                    }
                } catch (Exception e) {
                    log.error("消息处理异常，{}", e.getMessage());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

}
