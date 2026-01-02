package com.wsw.campushelp.controller;

import com.wsw.campushelp.entity.Task;
import com.wsw.campushelp.repository.TaskRepository;
import com.wsw.campushelp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {

    @Autowired
    private TaskService taskService;
    private TaskRepository taskRepository;

    /**
     * 发布求助接口
     * POST /api/tasks
     */
    @PostMapping
    public Map<String, Object> createTask(@RequestBody Task task) {
        Map<String, Object> result = new HashMap<>();
        try {
            Task newTask = taskService.createTask(task);
            result.put("code", 200);
            result.put("msg", "发布成功");
            result.put("data", newTask);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage()); // 例如：积分不足
        }
        return result;
    }

    /**
     * 获取大厅列表 (只看待接单)
     * GET /api/tasks/pending
     */
    @GetMapping("/pending")
    public Map<String, Object> getPendingTasks() {
        Map<String, Object> result = new HashMap<>();
        List<Task> list = taskService.getAllPendingTasks();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 获取所有列表 (管理员用)
     * GET /api/tasks
     */
    @GetMapping
    public Map<String, Object> getAllTasks() {
        Map<String, Object> result = new HashMap<>();
        List<Task> list = taskService.findAll();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
    /**
     * 接单接口
     * POST /api/tasks/{id}/accept?userId=xxx
     */
    @PostMapping("/{id}/accept")
    public Map<String, Object> acceptTask(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            taskService.acceptTask(id, userId);
            result.put("code", 200);
            result.put("msg", "接单成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 确认完成接口
     * POST /api/tasks/{id}/complete?userId=xxx
     */
    @PostMapping("/{id}/complete")
    public Map<String, Object> completeTask(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            taskService.completeTask(id, userId); // 这里的 userId 是发布者
            result.put("code", 200);
            result.put("msg", "已确认完成，积分已到账");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 删除任务接口 (管理员)
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "删除成功");
        return result;
    }

    /**
     * 搜索任务接口
     * GET /api/tasks/search?keyword=xxx
     */
    @GetMapping("/search")
    public Map<String, Object> searchTasks(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();

        // 🚨 变成直接呼叫 Repository 的新方法 (包含标签搜索)
        List<Task> list = taskRepository.searchTasks(keyword);

        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 放弃任务接口
     * POST /api/tasks/{id}/abandon?userId=xxx
     */
    @PostMapping("/{id}/abandon")
    public Map<String, Object> abandonTask(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            taskService.abandonTask(id, userId);
            result.put("code", 200);
            result.put("msg", "已放弃任务，违约金已扣除");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 删除任务接口
     * DELETE /api/tasks/{id}?userId=xxx
     */
    @DeleteMapping("/published/{id}") // <--- 改成这样，独一无二
    public Map<String, Object> deleteTask(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            taskService.deletePublishedTask(id, userId);
            result.put("code", 200);
            result.put("msg", "删除成功，积分已退还");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        }
        return result;
    }

}