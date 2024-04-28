package com.yanyan;

import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolService;
import com.yanyan.service.UserDetailService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YanyanApplicationTests {

	@Resource
	private MajorService majorService;
	@Resource
	private SchoolService schoolService;

	@Resource
	private UserDetailService userDetailService;

	@Test
	void testQueryMajorById() {
		System.out.println(majorService.queryMajorById(1L));
	}

	@Test
	void testQuerySchoolByName(){
		System.out.println(schoolService.querySchoolByName("上海理工大学"));
	}

	@Test
	void testQueryMajorByName(){
		System.out.println(majorService.queryMajorByName("中国语言文学"));
	}



}
