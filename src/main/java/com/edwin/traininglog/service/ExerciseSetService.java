package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.repository.ExerciseSetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseSetService {
    private final ExerciseSetRepository repository;

    public ExerciseSetService(ExerciseSetRepository repository) {
        this.repository = repository;
    }

    public List<ExerciseSet> queryTrainingSessions(String user) {
        return repository.findByUsername(user);
    }

    public List<ExerciseSet> getHeaviestSets(String user, int minReps) {
        return repository.findTopByUsernameAndRepetitionsGreaterThanEqualOrderByWeightDesc(user, minReps);
    }
}
