package com.wsw.campushelp.entity;

import jakarta.persistence.*; // 注意这里变了
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String location;
    // 标签字段 (例如: "跑腿,急单")
    private String tags;

    // 事件发生的时间
    @Column(name = "event_time")
    private String eventTime;

    // 悬赏积分
    @Column(name = "reward_points")
    private Integer rewardPoints;

    // 状态: PENDING, ACCEPTED, COMPLETED
    private String status;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Column(name = "accepter_id")
    private Long accepterId;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}