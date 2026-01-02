package com.wsw.campushelp.service;

import com.wsw.campushelp.entity.Task;
import com.wsw.campushelp.entity.User;
import com.wsw.campushelp.repository.TaskRepository;
import com.wsw.campushelp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 事务注解

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发布求助帖
     * 核心逻辑：判断积分是否足够 -> 扣除用户积分 -> 保存帖子
     */
    @Transactional // 加上这个注解，保证"扣分"和"存贴"要么都成功，要么都失败
    public Task createTask(Task task) {
        // 1. 获取发布者信息
        User publisher = userRepository.findById(task.getPublisherId()).orElse(null);
        if (publisher == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查积分是否足够支付悬赏
        if (publisher.getPoints() < task.getRewardPoints()) {
            throw new RuntimeException("积分不足，无法发布悬赏");
        }

        // 3. 扣除积分并保存用户
        publisher.setPoints(publisher.getPoints() - task.getRewardPoints());
        userRepository.save(publisher);

        // 4. 保存求助帖
        // 设置初始状态为 PENDING (待接单)
        task.setStatus("PENDING");
        return taskRepository.save(task);
    }

    /**
     * 查询所有待接单的帖子 (用于大厅显示)
     */
    public List<Task> getAllPendingTasks() {
        // 调用我们在 Repository 里写好的方法
        return taskRepository.findByStatus("PENDING");
    }

    /**
     * 查询所有帖子 (用于管理员管理)
     */
    public List<Task> findAll() {
        return taskRepository.findAllByOrderByCreateTimeDesc();
    }
    /**
     * 接单功能
     * @param taskId 任务ID
     * @param accepterId 接单人ID
     */
    public void acceptTask(Long taskId, Long accepterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 只有"待接单"的任务才能被接
        if (!"PENDING".equals(task.getStatus())) {
            throw new RuntimeException("该任务已被接单或已完成");
        }
        // 自己不能接自己的单
        if (task.getPublisherId().equals(accepterId)) {
            throw new RuntimeException("不能接自己发布的任务");
        }

        task.setStatus("ACCEPTED"); // 状态改为进行中
        task.setAccepterId(accepterId); // 记录接单人
        taskRepository.save(task);
    }

    /**
     * 完成任务 (结算积分)
     * @param taskId 任务ID
     * @param publisherId 操作人ID (必须是发布者自己才能点完成)
     */
    @Transactional // 涉及积分转账，必须加事务
    public void completeTask(Long taskId, Long publisherId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 安全检查：只有发布者本人才能确认完成
        if (!task.getPublisherId().equals(publisherId)) {
            throw new RuntimeException("无权操作，你不是发布者");
        }

        // 只有"进行中"的任务才能完成
        if (!"ACCEPTED".equals(task.getStatus())) {
            throw new RuntimeException("任务状态不正确");
        }

        // 1. 找到接单人
        User accepter = userRepository.findById(task.getAccepterId())
                .orElseThrow(() -> new RuntimeException("接单人数据异常"));

        // 2. 将悬赏积分加给接单人 (发布时已经扣过发布者的分了，这里只加分即可)
        accepter.setPoints(accepter.getPoints() + task.getRewardPoints());
        userRepository.save(accepter);

        // 3. 更新任务状态
        task.setStatus("COMPLETED");
        taskRepository.save(task);
    }

    /**
     * 删除任务 (管理员权限)
     */
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    /**
     * 搜索任务 (按标题或内容)
     */
    public List<Task> searchTasks(String keyword) {
        // 只要标题或内容包含关键字，都能搜出来
        return taskRepository.searchTasks(keyword);
    }

    /**
     * 放弃任务 (接单人后悔了)
     * 惩罚逻辑：扣除 10% 的悬赏积分作为违约金
     */
    @Transactional // 涉及扣分和改状态，必须加事务
    public void abandonTask(Long taskId, Long accepterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 1. 安全检查
        if (!accepterId.equals(task.getAccepterId())) {
            throw new RuntimeException("你不是该任务的接单人，无权放弃");
        }
        if (!"ACCEPTED".equals(task.getStatus())) {
            throw new RuntimeException("当前任务状态无法放弃");
        }

        // 2. 计算并扣除违约金 (10%)
        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        int penalty = (int) (task.getRewardPoints() * 0.1);
        // 如果算出来是 0 (比如悬赏只有5分)，建议至少扣 1 分，或者由你决定是否允许 0 惩罚
        if (penalty < 1 && task.getRewardPoints() > 0) penalty = 1;

        // 检查积分够不够扣 (可选逻辑：如果积分不够是扣成负数还是禁止放弃？这里我们假设允许扣成负数，或者你也可以加判断)
        // if (accepter.getPoints() < penalty) throw new RuntimeException("积分不足支付违约金");

        accepter.setPoints(accepter.getPoints() - penalty);
        userRepository.save(accepter);

        // 3. 任务回滚 (重新变回待接单，让别人能接)
        task.setStatus("PENDING");
        task.setAccepterId(null); // 清空接单人
        taskRepository.save(task);
    }

    /**
     * 发布者删除任务 (仅限待接单状态)
     * 逻辑：删除任务 + 退还悬赏积分
     */
    @Transactional
    public void deletePublishedTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 1. 鉴权：只能删自己发的
        if (!task.getPublisherId().equals(userId)) {
            throw new RuntimeException("无权删除他人的任务");
        }

        // 2. 状态检查：只能删待接单的 (PENDING)
        if (!"PENDING".equals(task.getStatus())) {
            throw new RuntimeException("任务已被接单或已完成，无法删除");
        }

        // 3. 退还积分
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPoints(user.getPoints() + task.getRewardPoints()); // 积分加回去
        userRepository.save(user);

        // 4. 删除任务
        taskRepository.delete(task);
    }
}
