package com.workloghub.repository;

import com.workloghub.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    Page<WorkLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<WorkLog> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    List<WorkLog> findByAuthorId(Long authorId);
}
