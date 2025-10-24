package com.edwin.traininglog.repository;

import com.edwin.traininglog.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByUsername(String username);

    List<ExerciseSet> findTopByUsernameAndRepetitionsGreaterThanEqualOrderByWeightDesc(String username, int repetitions);
}
