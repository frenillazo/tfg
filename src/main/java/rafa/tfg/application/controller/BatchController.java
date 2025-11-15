package rafa.tfg.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller REST para ejecutar jobs de batch y cargar datos desde JSON
 */
@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("importChampionJob")
    private final Job importChampionJob;

    @Qualifier("importItemJob")
    private final Job importItemJob;

    @Qualifier("importRuneJob")
    private final Job importRuneJob;

    @Qualifier("importSpellJob")
    private final Job importSpellJob;

    /**
     * Ejecutar el job de carga de Champions
     */
    @PostMapping("/load-champions")
    public ResponseEntity<Map<String, Object>> loadChampions() {
        return executeJob(importChampionJob, "Champions");
    }

    /**
     * Ejecutar el job de carga de Items
     */
    @PostMapping("/load-items")
    public ResponseEntity<Map<String, Object>> loadItems() {
        return executeJob(importItemJob, "Items");
    }

    /**
     * Ejecutar el job de carga de Runes
     */
    @PostMapping("/load-runes")
    public ResponseEntity<Map<String, Object>> loadRunes() {
        return executeJob(importRuneJob, "Runes");
    }

    /**
     * Ejecutar el job de carga de Spells
     */
    @PostMapping("/load-spells")
    public ResponseEntity<Map<String, Object>> loadSpells() {
        return executeJob(importSpellJob, "Spells");
    }

    /**
     * Ejecutar todos los jobs de carga de datos en secuencia
     */
    @PostMapping("/load-all")
    public ResponseEntity<Map<String, Object>> loadAll() {
        log.info("Iniciando carga completa de datos...");
        Map<String, Object> response = new LinkedHashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();

        // Ejecutar todos los jobs en secuencia
        results.add(executeJobAndGetResult(importChampionJob, "Champions"));
        results.add(executeJobAndGetResult(importItemJob, "Items"));
        results.add(executeJobAndGetResult(importRuneJob, "Runes"));
        results.add(executeJobAndGetResult(importSpellJob, "Spells"));

        // Verificar si alguno falló
        boolean allSuccess = results.stream()
                .allMatch(r -> "SUCCESS".equals(r.get("status")));

        response.put("overallStatus", allSuccess ? "SUCCESS" : "PARTIAL_SUCCESS");
        response.put("results", results);
        response.put("timestamp", new Date());

        log.info("Carga completa de datos finalizada. Estado: {}", response.get("overallStatus"));

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener el estado de los jobs ejecutados
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("message", "Batch jobs disponibles");
        status.put("champions", "/api/batch/load-champions");
        status.put("items", "/api/batch/load-items");
        status.put("runes", "/api/batch/load-runes");
        status.put("spells", "/api/batch/load-spells");
        status.put("all", "/api/batch/load-all");
        return ResponseEntity.ok(status);
    }

    /**
     * Método privado para ejecutar un job y devolver ResponseEntity
     */
    private ResponseEntity<Map<String, Object>> executeJob(Job job, String entityName) {
        Map<String, Object> response = executeJobAndGetResult(job, entityName);

        if ("SUCCESS".equals(response.get("status"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Método privado para ejecutar un job y devolver el resultado como Map
     */
    private Map<String, Object> executeJobAndGetResult(Job job, String entityName) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("entity", entityName);

        try {
            log.info("Iniciando carga de {}", entityName);

            // Crear parámetros únicos para permitir múltiples ejecuciones
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            BatchStatus batchStatus = jobExecution.getStatus();
            response.put("status", batchStatus.toString());
            response.put("startTime", jobExecution.getStartTime());
            response.put("endTime", jobExecution.getEndTime());
            response.put("exitCode", jobExecution.getExitStatus().getExitCode());

            // Agregar información de los steps
            Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
            List<Map<String, Object>> stepsInfo = new ArrayList<>();

            for (StepExecution stepExecution : stepExecutions) {
                Map<String, Object> stepInfo = new LinkedHashMap<>();
                stepInfo.put("stepName", stepExecution.getStepName());
                stepInfo.put("readCount", stepExecution.getReadCount());
                stepInfo.put("writeCount", stepExecution.getWriteCount());
                stepInfo.put("commitCount", stepExecution.getCommitCount());
                stepInfo.put("rollbackCount", stepExecution.getRollbackCount());
                stepInfo.put("status", stepExecution.getStatus().toString());
                stepsInfo.add(stepInfo);
            }

            response.put("steps", stepsInfo);

            if (batchStatus == BatchStatus.COMPLETED) {
                log.info("Carga de {} completada exitosamente", entityName);
                response.put("message", entityName + " cargados exitosamente");
            } else {
                log.warn("Carga de {} finalizada con estado: {}", entityName, batchStatus);
                response.put("message", "Carga de " + entityName + " finalizada con estado: " + batchStatus);
            }

        } catch (JobExecutionAlreadyRunningException e) {
            log.error("El job de {} ya está en ejecución", entityName, e);
            response.put("status", "ERROR");
            response.put("message", "El job ya está en ejecución");
            response.put("error", e.getMessage());
        } catch (JobRestartException e) {
            log.error("Error al reiniciar el job de {}", entityName, e);
            response.put("status", "ERROR");
            response.put("message", "Error al reiniciar el job");
            response.put("error", e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("El job de {} ya fue completado anteriormente", entityName, e);
            response.put("status", "ERROR");
            response.put("message", "El job ya fue completado anteriormente");
            response.put("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error ejecutando job de {}", entityName, e);
            response.put("status", "ERROR");
            response.put("message", "Error ejecutando el job");
            response.put("error", e.getMessage());
        }

        return response;
    }
}
