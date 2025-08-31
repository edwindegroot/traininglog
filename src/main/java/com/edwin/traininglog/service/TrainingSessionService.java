package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.repository.TrainingSessionRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingSessionService {
    private final TrainingSessionRepository repository;

    public TrainingSessionService(TrainingSessionRepository repository) {
        this.repository = repository;
    }

    public TrainingSession saveTrainingSession(TrainingSession exercise) {
        return repository.save(exercise);
    }

    public List<TrainingSession> queryTrainingSessions(String user) {
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setUsername(user);
        Example<TrainingSession> example = Example.of(trainingSession);
        return repository.findAll(example);
    }
}

