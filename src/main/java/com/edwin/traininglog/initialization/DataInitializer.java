package com.edwin.traininglog.initialization;

import com.edwin.traininglog.entity.Exercise;
import com.edwin.traininglog.repository.ExerciseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class DataInitializer implements CommandLineRunner {
    private final ExerciseRepository exerciseRepository;

    public DataInitializer(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Exercise squat = new Exercise();
        squat.setName("squat");

        Exercise benchPress = new Exercise();
        benchPress.setName("benchPress");

        Exercise deadlift = new Exercise();
        deadlift.setName("deadlift");

        exerciseRepository.save(squat);
        exerciseRepository.save(benchPress);
        exerciseRepository.save(deadlift);
    }
}
