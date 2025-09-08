package com.edwin.traininglog.controller;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.repository.ExerciseRepository;
import com.edwin.traininglog.repository.TrainingSessionRepository;
import com.edwin.traininglog.service.TrainingSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ExerciseSetControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Test
    void postExerciseSetAsJson() {
        // --- Prepare exercise in DB ---
        Exercise bench = new Exercise();
        bench.setName("Bench Press");
        Exercise savedExercise = exerciseRepository.save(bench);

        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setUsername("1");
        trainingSession.setNotes("SBD day");
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);

        String json = """
                {
                  "reps": 8,
                  "weight": 100,
                  "trainingSession": {
                    "id": %d
                  },
                  "exercise": {
                    "id": %d
                  }
                }
        """.formatted(savedSession.getId(), savedExercise.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<ExerciseSet> postResponse = restTemplate
                .postForEntity("/api/exercise-sets", entity, ExerciseSet.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        ExerciseSet set = postResponse.getBody();
        assertNotNull(set);

        // TODO make this work as expected.
        TrainingSession updatedSession = trainingSessionService.get(savedSession.getId());
        assertEquals(1, updatedSession.getSets().size());
    }
}