package com.workloghub.service;

import com.workloghub.dto.WorkLogRequest;
import com.workloghub.dto.WorkLogResponse;
import com.workloghub.entity.WorkLog;
import com.workloghub.entity.User;
import com.workloghub.repository.WorkLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final UserService userService;

    public WorkLogService(WorkLogRepository workLogRepository, UserService userService) {
        this.workLogRepository = workLogRepository;
        this.userService = userService;
    }

    public Page<WorkLog> getAllWorkLogs(Pageable pageable) {
        return workLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public WorkLog getWorkLogById(Long id) {
        return workLogRepository.findById(id).orElse(null);
    }

    public WorkLog createWorkLog(WorkLogRequest request, Long authorId) {
        User author = userService.findById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        WorkLog workLog = new WorkLog();
        workLog.setTitle(request.getTitle());
        workLog.setContent(request.getContent());
        workLog.setTechnologies(request.getTechnologies());
        workLog.setAuthor(author);
        workLog.setViewCount(0);
        workLog.setLikeCount(0);

        return workLogRepository.save(workLog);
    }

    public WorkLog updateWorkLog(Long id, WorkLogRequest request, Long userId) {
        WorkLog workLog = workLogRepository.findById(id).orElse(null);
        if (workLog == null) {
            throw new RuntimeException("WorkLog not found");
        }

        if (!workLog.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this worklog");
        }

        workLog.setTitle(request.getTitle());
        workLog.setContent(request.getContent());
        workLog.setTechnologies(request.getTechnologies());
        workLog.setUpdatedAt(LocalDateTime.now());

        return workLogRepository.save(workLog);
    }

    public void deleteWorkLog(Long id, Long userId) {
        WorkLog workLog = workLogRepository.findById(id).orElse(null);
        if (workLog == null) {
            throw new RuntimeException("WorkLog not found");
        }

        if (!workLog.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this worklog");
        }

        workLogRepository.deleteById(id);
    }

    public Page<WorkLog> getUserWorkLogs(Long userId, Pageable pageable) {
        return workLogRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
    }

    public void incrementViewCount(Long workLogId) {
        WorkLog workLog = workLogRepository.findById(workLogId).orElse(null);
        if (workLog != null) {
            workLog.setViewCount(workLog.getViewCount() + 1);
            workLogRepository.save(workLog);
        }
    }

    public WorkLog likeWorkLog(Long workLogId, Long userId) {
        WorkLog workLog = workLogRepository.findById(workLogId)
                .orElseThrow(() -> new RuntimeException("WorkLog not found"));
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (workLog.getLikedByUsers() != null && 
            workLog.getLikedByUsers().stream().anyMatch(u -> u.getId().equals(userId))) {
            throw new RuntimeException("WorkLog already liked by this user");
        }

        if (workLog.getLikedByUsers() == null) {
            workLog.setLikedByUsers(new java.util.ArrayList<>());
        }
        workLog.getLikedByUsers().add(user);
        workLog.setLikeCount(workLog.getLikeCount() + 1);
        return workLogRepository.save(workLog);
    }

    public WorkLog unlikeWorkLog(Long workLogId, Long userId) {
        WorkLog workLog = workLogRepository.findById(workLogId)
                .orElseThrow(() -> new RuntimeException("WorkLog not found"));

        if (workLog.getLikedByUsers() == null || 
            workLog.getLikedByUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new RuntimeException("WorkLog not liked by this user");
        }

        workLog.getLikedByUsers().removeIf(u -> u.getId().equals(userId));
        workLog.setLikeCount(Math.max(0, workLog.getLikeCount() - 1));
        return workLogRepository.save(workLog);
    }

    public WorkLogResponse convertToResponse(WorkLog workLog, Long currentUserId) {
        boolean isLiked = currentUserId != null && 
                workLog.getLikedByUsers() != null &&
                workLog.getLikedByUsers().stream()
                        .anyMatch(user -> user.getId().equals(currentUserId));

        return new WorkLogResponse(
            workLog.getId(),
            workLog.getTitle(),
            workLog.getContent(),
            workLog.getTechnologies(),
            userService.convertToResponse(workLog.getAuthor()),
            workLog.getViewCount(),
            workLog.getLikeCount(),
            isLiked,
            workLog.getCreatedAt(),
            workLog.getUpdatedAt()
        );
    }
}
