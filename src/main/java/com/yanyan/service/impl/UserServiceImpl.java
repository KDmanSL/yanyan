package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.User;
import com.yanyan.service.UserService;
import com.yanyan.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user】的数据库操作Service实现
* @createDate 2024-04-05 17:24:04
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




