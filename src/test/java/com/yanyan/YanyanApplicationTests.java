package com.yanyan;

import com.yanyan.dto.PostReplyDTO;
import com.yanyan.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
	void testQueryPostReplyWithUserInfoByPostId(){
		List<PostReplyDTO> postReplyDTOList = postReplyService.queryPostReplyWithUserInfoByPostId(1L);
		postReplyDTOList.forEach(System.out::println);
	}

	@Test
	void testQueryPostWithUserInfo() throws InterruptedException {
		postService.savePost2Redis(1L);
	}

}
