package com.yanyan;

import com.yanyan.service.MajorService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YanyanApplicationTests {

	@Resource
	private MajorService majorService;

	@Test
	void testQueryMajorById() {
		System.out.println(majorService.queryMajorById(1L));
	}

}
