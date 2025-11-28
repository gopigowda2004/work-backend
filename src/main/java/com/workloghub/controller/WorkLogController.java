package com.workloghub.controller;

import com.workloghub.dto.WorkLogRequest;
import com.workloghub.dto.WorkLogResponse;
import com.workloghub.entity.WorkLog;
import com.workloghub.service.WorkLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/worklog")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWorkLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<WorkLog> workLogs = workLogService.getAllWorkLogs(pageable);
            Page<WorkLogResponse> response = workLogs.map(w -> workLogService.convertToResponse(w, null));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching worklogs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching worklogs");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkLogById(@PathVariable Long id, Authentication authentication) {
        try {
            WorkLog workLog = workLogService.getWorkLogById(id);
            if (workLog == null) {
                return ResponseEntity.notFound().build();
            }
            workLogService.incrementViewCount(id);
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            return ResponseEntity.ok(workLogService.convertToResponse(workLog, userId));
        } catch (Exception e) {
            log.error("Error fetching worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching worklog");
        }
    }

    @PostMapping
    public ResponseEntity<?> createWorkLog(@RequestBody WorkLogRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            WorkLog workLog = workLogService.createWorkLog(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(workLogService.convertToResponse(workLog, userId));
        } catch (Exception e) {
            log.error("Error creating worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating worklog");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkLog(@PathVariable Long id, @RequestBody WorkLogRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            WorkLog workLog = workLogService.updateWorkLog(id, request, userId);
            return ResponseEntity.ok(workLogService.convertToResponse(workLog, userId));
        } catch (RuntimeException e) {
            log.error("Error updating worklog", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating worklog");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkLog(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            workLogService.deleteWorkLog(id, userId);
            return ResponseEntity.ok("WorkLog deleted successfully");
        } catch (RuntimeException e) {
            log.error("Error deleting worklog", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting worklog");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWorkLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<WorkLog> workLogs = workLogService.getUserWorkLogs(userId, pageable);
            Long currentUserId = authentication != null ? (Long) authentication.getPrincipal() : null;
            Page<WorkLogResponse> response = workLogs.map(w -> workLogService.convertToResponse(w, currentUserId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching user worklogs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user worklogs");
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeWorkLog(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            WorkLog workLog = workLogService.likeWorkLog(id, userId);
            return ResponseEntity.ok(workLogService.convertToResponse(workLog, userId));
        } catch (RuntimeException e) {
            log.error("Error liking worklog", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error liking worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error liking worklog");
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikeWorkLog(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            WorkLog workLog = workLogService.unlikeWorkLog(id, userId);
            return ResponseEntity.ok(workLogService.convertToResponse(workLog, userId));
        } catch (RuntimeException e) {
            log.error("Error unliking worklog", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error unliking worklog", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unliking worklog");
        }
    }
}
