package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.UserRole;
import com.yanyan.service.UserRoleService;
import com.yanyan.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user_role(用户角色表)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:17
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




