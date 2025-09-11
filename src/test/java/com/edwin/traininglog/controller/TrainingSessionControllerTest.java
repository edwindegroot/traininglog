package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.repository.ExerciseRepository;
import com.edwin.traininglog.repository.TrainingSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TrainingSessionControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Test
    void postSessionAsJson() {
        // --- Prepare exercise in DB ---
        Exercise bench = new Exercise();
        bench.setName("Bench Press");
        exerciseRepository.save(bench);
        long benchId = bench.getId();

        // --- Build JSON payload ---
        String json = """
        {
          "sessionDate": "2025-09-06",
          "notes": "Chest day",
          "username": "1",
          "sets": [
            { "exercise": { "id": %d }, "reps": 5, "weight": 100.0 },
            { "exercise": { "id": %d }, "reps": 8, "weight": 90.0 },
            { "exercise": { "id": %d }, "reps": 8, "weight": 90.0 },
            { "exercise": { "id": %d }, "reps": 8, "weight": 90.0 }
          ]
        }
        """.formatted(benchId, benchId, benchId, benchId);

        // --- POST JSON ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<TrainingSession> postResponse = restTemplate
                .postForEntity("/api/training-sessions", entity, TrainingSession.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        TrainingSession savedSession = postResponse.getBody();
        assertNotNull(savedSession);
        assertEquals("Chest day", savedSession.getNotes());
        assertEquals(4, savedSession.getSets().size());
        assertEquals(5, savedSession.getSets().get(0).getReps());
        assertEquals(8, savedSession.getSets().get(1).getReps());
        assertEquals(8, savedSession.getSets().get(2).getReps());
        assertEquals(8, savedSession.getSets().get(3).getReps());
        assertEquals(100.0, savedSession.getSets().get(0).getWeight());
        assertEquals(90.0, savedSession.getSets().get(1).getWeight());
        assertEquals(90.0, savedSession.getSets().get(2).getWeight());
        assertEquals(90.0, savedSession.getSets().get(3).getWeight());

        ResponseEntity<TrainingSession[]> getResponse = restTemplate.getForEntity(
                "/api/training-sessions/by-user/{id}", TrainingSession[].class, "1"
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        assertNotNull(getResponse.getBody());
        assertEquals(1, getResponse.getBody().length);

        // And delete it.
        restTemplate.delete("/api/training-sessions/{id}", savedSession.getId());

        getResponse = restTemplate.getForEntity(
                "/api/training-sessions/by-user/{id}", TrainingSession[].class, "1"
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(0, getResponse.getBody().length);
    }
}