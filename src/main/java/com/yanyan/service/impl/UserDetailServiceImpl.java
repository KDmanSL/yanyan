package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.UserDetail;
import com.yanyan.service.UserDetailService;
import com.yanyan.mapper.UserDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user_detail(考研信息)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:08
*/
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail>
    implements UserDetailService{

}




