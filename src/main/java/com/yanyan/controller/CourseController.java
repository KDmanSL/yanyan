package com.yanyan.controller;

import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import com.yanyan.service.MajorCourseService;
import com.yanyan.service.UserFavoritesService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/course")
@CrossOrigin
@RestController
public class CourseController {
    @Resource
    CourseService courseService;
    @Resource
    UserFavoritesService userFavoritesService;

    /**
     * 根据id查询课程
     * @param id 课程id
     * @return 课程信息
     */
    @GetMapping(value = "/{id}")
    public Result queryCourseById(@PathVariable("id") Long id){
        return courseService.queryCourseById(id);
    }

    /**
     * 查询全部课程列表
     * @return 课程列表
     */
    @GetMapping(value = "/listAll")
    public Result queryAllCoursesList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size){
        return courseService.queryAllCoursesList(current, size);
    }

    /**
     * 根据专业分类查课程列表（带分页）用于课程分类
     * @param majorId 专业id
     * @param current 当前页码
     * @param size 每页大小
     *
     * @return 课程列表
     */
    @GetMapping(value = "/listPage/{majorId}")
    public Result queryCoursesListByMajorIdWithPages(@PathVariable("majorId") Long majorId,
                                            @RequestParam(value = "current", defaultValue = "1") Integer current,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size){
        return courseService.queryCoursesListByMajorIdWithPages(majorId, current, size);
    }

    /**
     * 根据专业分类查课程列表（不带分页）用于用户推荐
     * @param majorId 专业id
     *
     * @return 课程列表
     */
    @GetMapping(value = "/list/{majorId}")
    public Result queryCoursesListByMajorId(@PathVariable("majorId") Long majorId){
        return courseService.queryCoursesListByMajorId(majorId);
    }

    /**
     * 添加课程收藏（若已收藏则取消收藏）
     * @param courseId 课程id
     * @return 执行结果
     */
    @PostMapping(value = "/favorite")
    public Result addFavorite(@RequestParam("courseId") Long courseId){
        return userFavoritesService.addUserFavorites(courseId);
    }

    /**
     * 根据课程名称模糊查询
     *
     * @param name 课程名称
     * @param current 当前页码
     * @param size 每页大小
     * @return 课程列表
     */
    @GetMapping(value = "/name/{name}")
    public Result queryCourseListByName(@PathVariable("name") String name,
                                        @RequestParam(value = "current", defaultValue = "1") Integer current,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size){
        return courseService.queryCourseListByName(name, current, size);
    }
}