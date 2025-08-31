package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.service.TrainingSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    private final TrainingSessionService exerciseService;

    public TrainingSessionController(TrainingSessionService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<TrainingSession> createExercise(@RequestBody TrainingSession session) {
        TrainingSession saved = exerciseService.saveTrainingSession(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/by-user/{user}")
    public ResponseEntity<List<TrainingSession>> get(@PathVariable String user) {
        List<TrainingSession> sessions = exerciseService.queryTrainingSessions(user);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
