package com.yanyan.controller;

import com.yanyan.dto.AddPostDTO;
import com.yanyan.dto.AddPostReplyDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.PostReplyService;
import com.yanyan.service.PostService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/post")
@CrossOrigin
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private PostReplyService postReplyService;
    /**
     * 查询所有帖子信息
     *
     * @param current 当前页
     * @return 所有帖子信息 + 总页数
     */
    @GetMapping(value = "/list")
    public Result queryAllPostList(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return postService.queryAllPostList(current);
    }

    /**
     * 查询某人发过的所有帖子
     *
     * @param userId 用户id
     * @param current 当前页
     * @return 帖子信息 + 总页数
     */
    @GetMapping(value = "/post/{userId}")
    public Result queryPostListByUserId(@PathVariable("userId") Long userId,
                                        @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return postService.queryPostListByUserId(userId, current);
    }

    /**
     * 查询某人发过的所有回复
     *
     * @param userId 用户id
     * @param current 当前页
     * @return 帖子信息 + 总页数
     */
    @GetMapping(value = "/reply/{userId}")
    public Result queryPostReplyListByUserId(@PathVariable("userId") Long userId,
                                             @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return postReplyService.queryPostReplyWithUserInfoByUserId(userId, current);
    }


    /**
     * 添加帖子
     * @param postDTO 帖子标题+内容
     * @return 添加结果
     */
    @PostMapping(value = "/add")
    public Result addPost(@RequestBody AddPostDTO postDTO) {
        return postService.addPost(postDTO);
    }

    /**
     * 添加帖子回复
     *
     * @param addPostReplyDTO 帖子id+回复内容
     * @return 添加结果
     */
    @PostMapping(value = "/reply/add")
    public Result addPostReply(@RequestBody AddPostReplyDTO addPostReplyDTO){
        return postReplyService.addPostReply(addPostReplyDTO);
    }

    /**
     * 删除帖子 只允许本人或者管理员删帖
     * @param postId 帖子id
     * @return 删除结果
     */
    @PostMapping("/post/delete")
    public Result deletePost(@RequestParam("postId") Long postId) {
        return postService.deletePost(postId);
    }

    /**
     * 删除帖子回复 只允许本人或者贴主或者管理员删帖
     * @param postReplyId 帖子回复id
     * @return 删除结果
     */
    @PostMapping("/reply/delete")
    public Result deletePostReply(@RequestParam("postReplyId") Long postReplyId) {
        return postReplyService.deletePostReply(postReplyId);
    }
    /**
     * 点赞
     * @param postId 帖子id
     */
    @PostMapping("/like")
    public Result like(@RequestParam("postId") Long postId) {
        return postService.addLike(postId);
    }
}
