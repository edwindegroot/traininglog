package com.edwin.traininglog.repository;

import com.edwin.traininglog.entity.RawTrainingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawTrainingLogRepository extends JpaRepository<RawTrainingLog, Long> {
    List<RawTrainingLog> findByStatus(RawTrainingLog.Status status);
}
