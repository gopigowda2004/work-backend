package com.workloghub.controller;

import com.workloghub.dto.CommentRequest;
import com.workloghub.entity.Comment;
import com.workloghub.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/worklog/{workLogId}")
    public ResponseEntity<?> getCommentsByWorkLog(@PathVariable Long workLogId) {
        try {
            List<Comment> comments = commentService.getCommentsByWorkLogId(workLogId);
            return ResponseEntity.ok(comments.stream()
                    .map(commentService::convertToResponse)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Error fetching comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching comments");
        }
    }

    @PostMapping("/worklog/{workLogId}")
    public ResponseEntity<?> createComment(@PathVariable Long workLogId, @RequestBody CommentRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Comment comment = commentService.createComment(workLogId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentService.convertToResponse(comment));
        } catch (Exception e) {
            log.error("Error creating comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating comment");
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            log.error("Error deleting comment", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting comment");
        }
    }
}
