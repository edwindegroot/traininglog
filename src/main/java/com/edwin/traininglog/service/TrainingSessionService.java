package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.repository.TrainingSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class TrainingSessionService {
    private final TrainingSessionRepository repository;

    public TrainingSessionService(TrainingSessionRepository repository) {
        this.repository = repository;
    }

    public TrainingSession saveExercise(TrainingSession exercise) {
        return repository.save(exercise);
    }
}

