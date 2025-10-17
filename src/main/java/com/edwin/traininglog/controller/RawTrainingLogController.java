package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.RawTrainingLog;
import com.edwin.traininglog.service.RawTrainingLogService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-training-logs")
public class RawTrainingLogController {
    private final RawTrainingLogService service;

    public RawTrainingLogController(RawTrainingLogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RawTrainingLog> createExercise(@RequestBody RawTrainingLog exercise) throws BadRequestException {
        RawTrainingLog saved = service.save(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<RawTrainingLog>> getAll() {
        List<RawTrainingLog> sessions = service.query();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
