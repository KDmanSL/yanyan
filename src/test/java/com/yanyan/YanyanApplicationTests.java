package com.yanyan;

import cn.hutool.core.date.DateUtil;
import com.yanyan.dto.PostReplyDTO;
import com.yanyan.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class YanyanApplicationTests {

	@Resource
	private MajorService majorService;
	@Resource
	private SchoolService schoolService;

	@Resource
	private UserDetailService userDetailService;

	@Resource
	private PostReplyService postReplyService;
	@Resource
	private PostService postService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private CourseService courseService;
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



	@Test
	void testQueryPostWithUserInfo() throws InterruptedException {
		postService.savePost2Redis(1L);
	}
	@Test
	void testUvSize(){
		String today = DateUtil.today();

		Long count = stringRedisTemplate.opsForHyperLogLog().size("user:uv:"+today);
		System.out.println(count);
	}

	@Test
	void testSaveCourses2Redis() throws InterruptedException {
		courseService.saveCourses2Redis(1L);
	}

}
