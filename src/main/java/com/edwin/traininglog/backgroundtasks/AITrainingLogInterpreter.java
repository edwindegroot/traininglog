package com.edwin.traininglog.backgroundtasks;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.entity.TrainingSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AITrainingLogInterpreter {
    private final String promptTemplate = """
            You are a workout log parser.
            Extract each training session into lines using this format:

            Session <number> | Notes | <text>
            Session <number> | <ExerciseName> | Sets=<number> | Reps=<number> | WeightKg=<number>

            Rules:
            - Always start each session with a Notes line. If no notes are present, output ""Notes |"".
            - Use PascalCase for exercise names (e.g., BenchPress, OverheadPress).
            - Only output lines, no explanations.

            Here follows an example for you:

            Example input:
            First training session (felt heavy overall):
            Bench press: 3 sets of 5 reps with 100kg
            Squat: 5 sets of 5 reps with 140kg
            Second training session (great form today):
            Overhead press: 3 sets of 5 reps with 100kg
            Deadlift: 5 sets of 5 reps with 140kg

            Example output:
            Session 1 | Notes | felt heavy overall
            Session 1 | BenchPress | Sets=3 | Reps=5 | WeightKg=100
            Session 1 | Squat | Sets=5 | Reps=5 | WeightKg=140
            Session 2 | Notes | great form today
            Session 2 | OverheadPress | Sets=3 | Reps=5 | WeightKg=100
            Session 2 | Deadlift | Sets=5 | Reps=5 | WeightKg=140

            Now generate the lines as described based on this raw text:
            """;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final ObjectMapper mapper;

    public AITrainingLogInterpreter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<TrainingSession> interpret(String text) {
        String fullPrompt = promptTemplate + "\n" + text;

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "mistral");
        bodyMap.put("prompt", fullPrompt);
        bodyMap.put("stream", "false");

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:11434/api/")
                .build();
        String jsonResponse = webClient.post()
                .uri("generate")
                .body(BodyInserters.fromValue(bodyMap))
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofMinutes(2));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            String message = String.format("Invalid json: %s", jsonResponse);
            logger.error(message, e);
            return new ArrayList<>();
        }
        String aiResponse = root.path("response").asText();
        return getTrainingSessions(aiResponse);
    }

    private List<TrainingSession> getTrainingSessions(String aiResponse) {
        String[] lines = aiResponse.split("\n");

        List<TrainingSession> sessions = new ArrayList<>();
        TrainingSession session = new TrainingSession();
        sessions.add(session);
        Integer currentSession = null;

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split("\\|");
            if (parts.length < 3) continue;

            int sessionNr = Integer.parseInt(parts[0].trim().replace("Session ", ""));
            if (currentSession == null || !currentSession.equals(sessionNr)) {
                session = new TrainingSession();
                sessions.add(session);
                currentSession = sessionNr;
            }

            String partType = parts[1].trim();
            if ("Notes".equalsIgnoreCase(partType)) {
                session.setNotes(parts[2].trim());
            } else {
                String exerciseStr = partType;
                int sets = Integer.parseInt(parts[2].trim().replace("Sets=", ""));
                int reps = Integer.parseInt(parts[3].trim().replace("Reps=", ""));
                double weight = Double.parseDouble(parts[4].trim().replace("WeightKg=", ""));

                Exercise exercise = new Exercise();
                try {
//                    exercise = Exercise.parse(exerciseStr);
                } catch (IllegalArgumentException e) {
                    continue; // Unknown exercise
                }

                for (int i = 0; i < sets; i++) {
                    ExerciseSet set = new ExerciseSet();
                    set.setExercise(exercise);
                    set.setRepetitions(reps);
                    set.setWeight(weight);
                    session.getSets().add(set);
                }
            }
        }

        return sessions;
    }
}
