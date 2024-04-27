package com.yanyan.service;

import com.yanyan.domain.Major;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_major(专业表)】的数据库操作Service
* @createDate 2024-04-05 17:23:43
*/
public interface MajorService extends IService<Major> {
    Result queryMajorById(Long id);

    void saveMajor2Redis(Long expireSeconds) throws InterruptedException;
}
