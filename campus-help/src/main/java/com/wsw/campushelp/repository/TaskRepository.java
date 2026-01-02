package com.wsw.campushelp.repository;

import com.wsw.campushelp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 1. 根据状态查找任务 (用于首页显示 "待接单" 的任务)
    List<Task> findByStatus(String status);

    // 2. 查找某个用户发布的所有任务 (用于"我的发布")
    List<Task> findByPublisherId(Long publisherId);

    // 3. 查找某个用户接受的所有任务 (用于"我的接受")
    List<Task> findByAccepterId(Long accepterId);

    // 同时搜索标题、内容、标签
    @Query("SELECT t FROM Task t WHERE t.title LIKE %?1% OR t.content LIKE %?1% OR t.tags LIKE %?1%")
    List<Task> searchTasks(String keyword);


    // 5. 根据时间倒序查询所有任务 (让最新的显示在最前面)
    List<Task> findAllByOrderByCreateTimeDesc();
}