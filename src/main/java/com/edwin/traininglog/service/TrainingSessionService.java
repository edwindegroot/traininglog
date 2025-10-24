package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.repository.TrainingSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingSessionService {
    private final TrainingSessionRepository repository;

    public TrainingSessionService(TrainingSessionRepository repository) {
        this.repository = repository;
    }

    public TrainingSession saveTrainingSession(TrainingSession session) {
        String username = session.getUsername();
        session.getSets().forEach(set -> set.setUsername(username));
        return repository.save(session);
    }

    public List<TrainingSession> queryTrainingSessions(String user) {
        return repository.findByUsername(user);
    }

    public TrainingSession get(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }
}

