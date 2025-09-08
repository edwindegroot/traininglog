package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.service.ExerciseSetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercise-sets")
public class ExerciseSetController {
    private final ExerciseSetService service;

    public ExerciseSetController(ExerciseSetService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExerciseSet> create(@RequestBody ExerciseSet entity) {
        ExerciseSet saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
