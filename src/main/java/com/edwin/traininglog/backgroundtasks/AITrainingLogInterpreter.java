package com.edwin.traininglog.backgroundtasks;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.entity.ExerciseSet;
import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.service.ExerciseService;
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
    private static final String PROMPT_TEMPLATE = """
            You are a workout log parser.
            Extract each training session into lines using this format:

            Session <number> | Notes | <text>
            Session <number> | <ExerciseName> | Sets=<number> | Reps=<number> | WeightKg=<number>

            Rules:
            - Always start each session with a Notes line. If no notes are present, output ""Notes |"".
            - Notes should be constructed from the general text that is in the general text besides the sets, exercise and reps.
            - Exercise names should be translated to one of the following options (EXACT MATCH): %s
            - If an exercise is not in the list just mentioned, ignore it.
            - Only output lines, no explanations.

            Here follows an example for you:

            Example input:
            First training session (felt heavy overall):
            Bench press: 3 sets of 5 reps with 100kg
            Squat: 5 sets of 5 reps with 140kg
            Second training session (great form today):
            Overhead press: 3 sets of 5 reps with 100kg
            Deadlift: 5 sets of 5 reps with 140kg

            Example output (IMPORTANT: exercise names may differ from the actual exercise names you should use, as mentioned in the rules):
            Session 1 | Notes | felt heavy overall
            Session 1 | benchPress | Sets=3 | Reps=5 | WeightKg=100
            Session 1 | squat | Sets=5 | Reps=5 | WeightKg=140
            Session 2 | Notes | great form today
            Session 2 | benchPress | Sets=3 | Reps=5 | WeightKg=100
            Session 2 | squat | Sets=5 | Reps=5 | WeightKg=140
            
            Available exercise names are

            Now generate the lines as described based on this raw text: %s
            """;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final ExerciseService exerciseService;

    public AITrainingLogInterpreter(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public List<TrainingSession> interpret(String text) {
        Map<String, Exercise> nameToExerciseMap = new HashMap<>();
        exerciseService.queryExercises().forEach(e -> nameToExerciseMap.put(e.getName(), e));
        String fullPrompt = PROMPT_TEMPLATE.formatted(new ArrayList<>(nameToExerciseMap.keySet()), text);

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "mistral");
        bodyMap.put("prompt", fullPrompt);
        bodyMap.put("stream", false);

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
        JsonNode root;
        try {
            root = mapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            String message = String.format("Invalid json: %s", jsonResponse);
            logger.error(message, e);
            return new ArrayList<>();
        }
        String aiResponse = root.path("response").asText();

        try {
            return getTrainingSessions(aiResponse, nameToExerciseMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<TrainingSession> getTrainingSessions(String aiResponse,
                                                      Map<String, Exercise> exerciseMap) {
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
                int sets = Integer.parseInt(parts[2].trim().replace("Sets=", ""));
                int reps = Integer.parseInt(parts[3].trim().replace("Reps=", ""));
                double weight = Double.parseDouble(parts[4].trim().replace("WeightKg=", ""));

                Exercise exercise;
                try {
                    exercise = exerciseMap.get(partType);
                } catch (IllegalArgumentException e) {
                    continue; // Unknown exercise
                }

                for (int i = 0; i < sets; i++) {
                    ExerciseSet set = new ExerciseSet();
                    set.setExercise(exercise);
                    set.setRepetitions(reps);
                    set.setWeight(weight);
                    if (session.getSets() == null) session.setSets(new ArrayList<>());
                    session.getSets().add(set);
                }
            }
        }

        return sessions;
    }
}
