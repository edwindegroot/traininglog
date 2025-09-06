package com.edwin.traininglog.service;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService{
    private final ExerciseRepository repository;

    public ExerciseService(ExerciseRepository repository) {
        this.repository = repository;
    }

    public Exercise saveExercise(Exercise exercise) {
        return repository.save(exercise);
    }

    public List<Exercise> queryExercises() {
        return repository.findAll();
    }
}
