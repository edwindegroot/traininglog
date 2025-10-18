package com.edwin.traininglog.backgroundtasks;

import com.edwin.traininglog.entity.RawTrainingLog;
import com.edwin.traininglog.entity.TrainingSession;
import com.edwin.traininglog.service.RawTrainingLogService;
import com.edwin.traininglog.service.TrainingSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final RawTrainingLogService rawTrainingLogService;
    private final TrainingSessionService trainingSessionService;
    private final AITrainingLogInterpreter interpreter;

    public ScheduledTasks(RawTrainingLogService rawTrainingLogService,
                          TrainingSessionService trainingSessionService,
                          AITrainingLogInterpreter interpreter) {
        this.rawTrainingLogService = rawTrainingLogService;
        this.trainingSessionService = trainingSessionService;
        this.interpreter = interpreter;
    }

    @Scheduled(fixedRate = 5000)
    public void processRawTrainingLogs() {
        List<RawTrainingLog> logs = rawTrainingLogService.query(RawTrainingLog.Status.PENDING);
        for (RawTrainingLog log : logs) {
            logger.info(
                    "{} submitting raw training log with id {}",
                    dateFormat.format(new Date()), log.getId()
            );
            log.setStatus(RawTrainingLog.Status.IN_PROGRESS);
            rawTrainingLogService.save(log);
            executorService.submit(() -> process(log));
        }
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupRawTrainingLogTable() {
        List<RawTrainingLog> logs = rawTrainingLogService.query(RawTrainingLog.Status.PENDING);
        for (RawTrainingLog log : logs) {
            LocalDateTime doc = log.getCreatedAt();
            if (log.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(10))) {
                logger.info("Deleting raw training log {}, created at {}", log.getId(), doc);
                rawTrainingLogService.delete(log);
            }
        }
        logger.info("The time is now {}", dateFormat.format(new Date()));
    }

    private void process(RawTrainingLog log) {
        logger.info("Processing log with id {}", log.getId());
        List<TrainingSession> sessions = interpreter.interpret(log.getText());
        sessions.forEach(trainingSessionService::saveTrainingSession);
        logger.info("Persisted {} interpreted training sessions", sessions.size());
        log.setStatus(RawTrainingLog.Status.PROCESSED);
        rawTrainingLogService.save(log);
    }
}
