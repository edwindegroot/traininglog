package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise saved = service.saveExercise(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAll() {
        List<Exercise> sessions = service.queryExercises();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
