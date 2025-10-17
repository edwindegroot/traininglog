package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.RawTrainingLog;
import com.edwin.traininglog.repository.RawTrainingLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RawTrainingLogService {
    private final RawTrainingLogRepository repository;

    public RawTrainingLogService(RawTrainingLogRepository repository) {
        this.repository = repository;
    }

    public RawTrainingLog save(RawTrainingLog entity) {
        return repository.save(entity);
    }

    public List<RawTrainingLog> query() {
        return query(null);
    }

    public List<RawTrainingLog> query(RawTrainingLog.Status status) {
        return repository.findByStatus(status);
    }

    public void delete(RawTrainingLog entity) {
        repository.delete(entity);
    }
}
