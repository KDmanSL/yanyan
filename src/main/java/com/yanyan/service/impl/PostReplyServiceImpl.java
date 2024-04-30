package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.PostReply;
import com.yanyan.dto.AddPostReplyDTO;
import com.yanyan.dto.PostReplyDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.PostReplyService;
import com.yanyan.mapper.PostReplyMapper;
import com.yanyan.service.PostService;
import com.yanyan.utils.RegexUtils;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yanyan.utils.SystemConstants.DEFAULT_PAGE_SIZE;

/**
* @author 韶光善良君
* @description 针对表【yy_post_reply(帖子回复表)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:53
*/
@Service
public class PostReplyServiceImpl extends ServiceImpl<PostReplyMapper, PostReply>
    implements PostReplyService{
    @Resource
    PostReplyMapper postReplyMapper;

    @Resource
    PostService postService;

    @Override
    public Result queryPostReplyWithUserInfoByUserId(Long userId, Integer current) {
        int start = (current - 1) * DEFAULT_PAGE_SIZE;
        int end = current * DEFAULT_PAGE_SIZE - 1;

        List<PostReplyDTO> postReplyList = postReplyMapper.selectPostReplyWithUserInfoByUserId(userId);
        if(postReplyList != null && !postReplyList.isEmpty()){
            Long totalPage = (long) Math.ceil((double) postReplyList.size() / DEFAULT_PAGE_SIZE);
            // 检查分页索引，防止越界
            int listSize = postReplyList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }
            List<PostReplyDTO> nowPostReplyList = postReplyList.subList(start, end+1);
            return Result.ok(nowPostReplyList, totalPage);
        }
        return Result.fail("该用户没有任何回复");
    }

    @Override
    public Result addPostReply(AddPostReplyDTO addPostReplyDTO) throws InterruptedException {
        String content = addPostReplyDTO.getContent();
        // 验证帖子回复内容
        if (RegexUtils.isPostContentInvalid(content)) {
            return Result.fail("帖子内容不符合格式要求");
        }


        Long userId = UserHolder.getUser().getId();

        PostReply postReply = new PostReply();
        postReply.setPostid(addPostReplyDTO.getPostid());
        postReply.setContent(content);
        postReply.setUserid(userId);
        save(postReply);
        // 帖子缓存重建
        postService.savePost2Redis(30L);
        return Result.ok("回复发表成功");
    }


}




