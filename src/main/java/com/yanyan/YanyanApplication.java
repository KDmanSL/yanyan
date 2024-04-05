package com.yanyan;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yanyan.mapper")
public class YanyanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YanyanApplication.class, args);
	}

}
