package rafa.tfg.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rafa.tfg.service.DataLoaderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoaderRunner implements CommandLineRunner {

    private final DataLoaderService dataLoaderService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting data load ===");

        try {
            dataLoaderService.loadChampions();
            dataLoaderService.loadItems();
            log.info("=== Data load completed successfully ===");
        } catch (Exception e) {
            log.error("Error loading data", e);
            throw e;
        }
    }
}
