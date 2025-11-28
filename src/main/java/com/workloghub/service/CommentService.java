package com.workloghub.service;

import com.workloghub.dto.CommentRequest;
import com.workloghub.dto.CommentResponse;
import com.workloghub.entity.Comment;
import com.workloghub.entity.WorkLog;
import com.workloghub.entity.User;
import com.workloghub.repository.CommentRepository;
import com.workloghub.repository.WorkLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final WorkLogRepository workLogRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, WorkLogRepository workLogRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.workLogRepository = workLogRepository;
        this.userService = userService;
    }

    public List<Comment> getCommentsByWorkLogId(Long workLogId) {
        return commentRepository.findByWorkLogIdOrderByCreatedAtDesc(workLogId);
    }

    public Comment createComment(Long workLogId, Long userId, CommentRequest request) {
        WorkLog workLog = workLogRepository.findById(workLogId).orElse(null);
        User author = userService.findById(userId);

        if (workLog == null || author == null) {
            throw new RuntimeException("WorkLog or User not found");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setWorkLog(workLog);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.deleteById(commentId);
    }

    public CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            userService.convertToResponse(comment.getAuthor()),
            comment.getCreatedAt()
        );
    }
}
