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

    private final TrainingSessionService service;

    public TrainingSessionController(TrainingSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TrainingSession> createExercise(@RequestBody TrainingSession session) {
        TrainingSession saved = service.saveTrainingSession(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/by-user/{user}")
    public ResponseEntity<List<TrainingSession>> get(@PathVariable String user) {
        List<TrainingSession> sessions = service.queryTrainingSessions(user);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
