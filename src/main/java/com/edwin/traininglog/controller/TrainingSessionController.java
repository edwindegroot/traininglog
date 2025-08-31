package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.service.TrainingSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    private final TrainingSessionService exerciseService;

    public TrainingSessionController(TrainingSessionService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<TrainingSession> createExercise(@RequestBody TrainingSession session) {
        TrainingSession saved = exerciseService.saveExercise(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
