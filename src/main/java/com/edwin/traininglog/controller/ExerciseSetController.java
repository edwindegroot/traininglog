package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.service.ExerciseSetService;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sets")
public class ExerciseSetController {
    private final ExerciseSetService service;

    public ExerciseSetController(ExerciseSetService service) {
        this.service = service;
    }

    @GetMapping("/by-user/{user}")
    public ResponseEntity<List<ExerciseSet>> get(@PathVariable String user) {
        List<ExerciseSet> sessions = service.queryTrainingSessions(user);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @GetMapping("/by-user/{user}/heaviest")
    public ResponseEntity<List<ExerciseSet>> get(@PathVariable String user,
                                                 @RequestParam @DefaultValue("0") int minReps) {
        List<ExerciseSet> sessions = service.getHeaviestSets(user, minReps);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
