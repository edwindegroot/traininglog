package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.repository.ExerciseSetRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseSetService {
    private final ExerciseSetRepository repository;

    public ExerciseSetService(ExerciseSetRepository repository) {
        this.repository = repository;
    }

    public ExerciseSet save(ExerciseSet entity) {
        return repository.save(entity);
    }
}
