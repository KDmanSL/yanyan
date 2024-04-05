package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Post;
import com.yanyan.service.PostService;
import com.yanyan.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:50
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




