package com.yanyan.controller;

import com.yanyan.dto.AddPostDTO;
import com.yanyan.dto.Result;
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
    @GetMapping(value = "/list/user")
    public Result queryPostListByUserId(@RequestParam(value = "userId") Long userId,
                                        @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return postService.queryPostListByUserId(userId, current);
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
}
