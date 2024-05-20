package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Post;
import com.yanyan.dto.*;
import com.yanyan.mapper.PostReplyMapper;
import com.yanyan.service.PostReplyService;
import com.yanyan.service.PostService;
import com.yanyan.mapper.PostMapper;
import com.yanyan.service.UserService;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RegexUtils;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        implements PostService {
    @Resource
    private PostMapper postMapper;
    @Resource
    PostReplyMapper postReplyMapper;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public Result queryAllPostList(Integer current) {
        int start = (current - 1) * DEFAULT_PAGE_SIZE;
        int end = current * DEFAULT_PAGE_SIZE - 1;
        //1.从redis查询帖子列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(POST_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<PostDTO> postsList = listCache.stream()
                    .map(str -> (PostDTO) JSONUtil.toBean(str, PostDTO.class, true))
                    .sorted(Comparator.comparing(PostDTO::getPostdate).reversed())
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
            List<PostDTO> nowPageList = postsList.subList(start, end + 1);
            return Result.ok(nowPageList, totalPage);
        }
        //3.缓存为空,缓存重建
        savePost2Redis(Post_ALL_LIST_TTL);
        return queryAllPostList(current);
//
//        List<Post> postsList =  query().orderByDesc("postDate").list();
//        if (postsList==null) {
//            return Result.fail("查询帖子列表失败");
//        }
//
//        // 将数据写入redis
//        List<String> strList = postsList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
//        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.POST_ALL_LIST_KEY, strList);
//        stringRedisTemplate.expire(POST_ALL_LIST_KEY, POST_ALL_LIST_TTL, TimeUnit.MINUTES);
//        Long totalPage = (long) Math.ceil((double) postsList.size() / DEFAULT_PAGE_SIZE);
//        start = Math.max(start, 0);
//        end = Math.min(end, postsList.size() - 1);
//        if (start >= postsList.size()) {
//            return Result.fail("超出页面请求范围");
//        }
//        // 返回当前页数据
//        List<Post> nowPageList = postsList.subList(start, end + 1);
//
//        return Result.ok(nowPageList, totalPage);
    }


    @Override
    public void savePost2Redis(Long expireSeconds) {
        // 添加分布式锁
        RLock lock = redissonClient.getLock(CACHE_POST_LOCK_KEY);
        boolean isLock = lock.tryLock();
        if (isLock) {
            try {
                List<PostDTO> postList = postMapper.selectPostWithUserInfo();
                for (PostDTO postDTO : postList) {
                    Long postId = postDTO.getId();
                    List<PostReplyDTO> postReplyList = postReplyMapper.selectPostReplyWithUserInfo(postId);
                    postDTO.setPostReplyList(postReplyList);
                }
                // 检查数据库缓存是否存在，存在则删除缓存
                if (stringRedisTemplate.hasKey(POST_ALL_LIST_KEY)) {
                    stringRedisTemplate.delete(POST_ALL_LIST_KEY);
                }
                // 将数据写入redis
                List<String> strList = postList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
                stringRedisTemplate.opsForList().rightPushAll(RedisConstants.POST_ALL_LIST_KEY, strList);
                stringRedisTemplate.expire(POST_ALL_LIST_KEY, expireSeconds, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                //释放锁
                lock.unlock();
            }
        }
    }

    @Override
    public Result queryPostListByUserId(Long userId, Integer current) {
        int start = (current - 1) * DEFAULT_PAGE_SIZE;
        int end = current * DEFAULT_PAGE_SIZE - 1;
        //1.从redis查询帖子列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(POST_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<PostDTO> postsList = listCache.stream()
                    .map(str -> (PostDTO) JSONUtil.toBean(str, PostDTO.class, true))
                    .filter(postDTO -> postDTO.getUserid().equals(userId))
                    .sorted(Comparator.comparing(PostDTO::getPostdate).reversed())
                    .collect(Collectors.toList());
            if(postsList.isEmpty()){
                return Result.fail("该用户没有帖子");
            }
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
            List<PostDTO> nowPageList = postsList.subList(start, end + 1);
            return Result.ok(nowPageList, totalPage);
        }
        //3.缓存为空,缓存重建
        savePost2Redis(Post_ALL_LIST_TTL);
        return queryAllPostList(current);
    }

    @Override
    public Result addPost(AddPostDTO postDTO) {
        Long userId;
        try {
            userId = UserHolder.getUser().getId();
        } catch (Exception e) {
            return Result.fail("用户未登录");
        }
        // 校验帖子标题
        String title = postDTO.getTitle();
        if (RegexUtils.isPostTitleInvalid(title)) {
            return Result.fail("帖子标题不符合格式要求");
        }
        // 校验帖子内容
        String content = postDTO.getContent();
        if (RegexUtils.isPostContentInvalid(content)) {
            return Result.fail("帖子内容不符合格式要求");
        }

        Post post = new Post();
        post.setUserid(userId);
        post.setTitle(title);
        post.setContent(content);
        save(post);
        // 缓存重建
        savePost2Redis(Post_ALL_LIST_TTL);
        return Result.ok("帖子发表成功");

    }

    @Override
    public Result deletePost(Long postId) {
        // 核验身份 帖子主人或者系统管理员允许删除帖子
        UserDTO user;
        Long userId;
        try {
            user = UserHolder.getUser();
            userId = user.getId();
        }catch (Exception e){
            return Result.fail("用户未登录");
        }
        Post post = getById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        Long userId2 = post.getUserid();
        String role = user.getRole();
        if (role.equals("adm") || userId.equals(userId2)) {
            removeById(postId);
            // 缓存重建
            savePost2Redis(Post_ALL_LIST_TTL);
            return Result.ok("帖子删除成功");
        }
        return Result.fail("你无法删除该帖子");
    }
}




