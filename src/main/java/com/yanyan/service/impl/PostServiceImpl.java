package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Post;
import com.yanyan.domain.School;
import com.yanyan.dto.PostDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.PostService;
import com.yanyan.mapper.PostMapper;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RegexUtils;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yanyan.utils.RedisConstants.*;
import static com.yanyan.utils.SystemConstants.DEFAULT_PAGE_SIZE;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:50
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // TODO 查询所有帖子，查询某个用户的所有帖子，添加帖子信息和发送者
    @Override
    public Result queryAllPostList(Integer current) {
        int start = (current - 1) * DEFAULT_PAGE_SIZE;
        int end = current * DEFAULT_PAGE_SIZE - 1;
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(POST_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<Post> postsList = listCache.stream()
                    .map(str -> (Post) JSONUtil.toBean(str, Post.class, true))
                    .sorted(Comparator.comparing(Post::getPostdate).reversed())
                    .collect(Collectors.toList());
            Long totalPage = (long) Math.ceil((double) postsList.size() / DEFAULT_PAGE_SIZE);
            // 检查分页索引，防止越界
            int listSize = postsList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<Post> nowPageList = postsList.subList(start, end + 1);
            return Result.ok(nowPageList, totalPage);
        }
        //3.缓存为空，查询数据库
        List<Post> postsList =  query().orderByDesc("postDate").list();
        if (postsList==null) {
            return Result.fail("查询院校列表失败");
        }

        // 将数据写入redis
        List<String> strList = postsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.POST_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(POST_ALL_LIST_KEY, POST_ALL_LIST_TTL, TimeUnit.MINUTES);
        Long totalPage = (long) Math.ceil((double) postsList.size() / DEFAULT_PAGE_SIZE);
        start = Math.max(start, 0);
        end = Math.min(end, postsList.size() - 1);
        if (start >= postsList.size()) {
            return Result.fail("超出页面请求范围");
        }
        // 返回当前页数据
        List<Post> nowPageList = postsList.subList(start, end + 1);

        return Result.ok(nowPageList, totalPage);
    }

    @Override
    public Result queryPostListByUserId(Long userId,Integer current) {
        return null;
    }

    @Override
    public Result addPost(PostDTO postDTO) {
        // 校验帖子标题
        String title = postDTO.getTitle();
        if(RegexUtils.isPostTitleInvalid(title)){
            return Result.fail("帖子标题不符合格式要求");
        }
        // 校验帖子内容
        String content = postDTO.getContent();
        if(RegexUtils.isPostContentInvalid(content)){
            return Result.fail("帖子内容不符合格式要求");
        }

        Long userId = UserHolder.getUser().getId();
        Post post = new Post();
        post.setUserid(userId);
        post.setTitle(title);
        post.setContent(content);
        save(post);
        // TODO 缓存重建
        return Result.ok("帖子发表成功");

    }
}




