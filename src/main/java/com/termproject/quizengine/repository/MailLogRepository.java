package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.MailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailLogRepository extends JpaRepository<MailLog, Long>{

    Page<MailLog> findAll(Pageable pageable);

    Page<MailLog> findByUserId(Long id, Pageable pageable);

    Optional<MailLog> findById(Long id);

    Optional<MailLog> findByUuid(String uuid);

}
