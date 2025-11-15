package rafa.tfg.infrastructure.batch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import rafa.tfg.domain.model.Rune;
import rafa.tfg.domain.model.RunePath;
import rafa.tfg.domain.service.RunePathService;
import rafa.tfg.domain.service.RuneService;
import rafa.tfg.infrastructure.batch.dto.RunePathJsonDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuración de Spring Batch para cargar Runes desde JSON
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RuneBatchConfig {

    private final RunePathService runePathService;
    private final RuneService runeService;
    private final ObjectMapper objectMapper;

    @Bean
    public ItemReader<RunePathJsonDTO> runePathReader() {
        return new ItemReader<>() {
            private List<RunePathJsonDTO> runePaths;
            private int currentIndex = 0;

            @Override
            public RunePathJsonDTO read() throws IOException {
                if (runePaths == null) {
                    loadRunePaths();
                }

                if (currentIndex < runePaths.size()) {
                    return runePaths.get(currentIndex++);
                }

                return null; // End of data
            }

            private void loadRunePaths() throws IOException {
                ClassPathResource resource = new ClassPathResource("data/runesReforged.json");
                runePaths = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<RunePathJsonDTO>>() {});
                log.info("Loaded {} rune paths from JSON", runePaths.size());
            }
        };
    }

    @Bean
    public ItemProcessor<RunePathJsonDTO, RunePath> runePathProcessor() {
        return runePathDTO -> {
            log.debug("Processing rune path: {}", runePathDTO.getName());

            // Create RunePath first
            RunePath runePath = RunePath.builder()
                    .pathId(runePathDTO.getId())
                    .key(runePathDTO.getKey())
                    .icon(runePathDTO.getIcon())
                    .name(runePathDTO.getName())
                    .build();

            // Save RunePath to get ID
            RunePath savedRunePath = runePathService.create(runePath);

            // Create and save Runes for this path
            if (runePathDTO.getSlots() != null) {
                int slotPosition = 0;
                for (RunePathJsonDTO.SlotDTO slot : runePathDTO.getSlots()) {
                    if (slot.getRunes() != null) {
                        for (RunePathJsonDTO.RuneJsonDTO runeDTO : slot.getRunes()) {
                            Rune rune = Rune.builder()
                                    .runeId(runeDTO.getId())
                                    .key(runeDTO.getKey())
                                    .icon(runeDTO.getIcon())
                                    .name(runeDTO.getName())
                                    .shortDesc(runeDTO.getShortDesc())
                                    .longDesc(runeDTO.getLongDesc())
                                    .runePathId(savedRunePath.getId())
                                    .slotPosition(slotPosition)
                                    .build();

                            runeService.create(rune);
                            log.debug("Saved rune: {}", rune.getName());
                        }
                    }
                    slotPosition++;
                }
            }

            return savedRunePath;
        };
    }

    @Bean
    public ItemWriter<RunePath> runePathWriter() {
        return runePaths -> {
            // RunePaths and Runes already saved in processor
            log.info("Completed processing {} rune paths", runePaths.size());
        };
    }

    @Bean
    public Step runeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("runeStep", jobRepository)
                .<RunePathJsonDTO, RunePath>chunk(5, transactionManager)
                .reader(runePathReader())
                .processor(runePathProcessor())
                .writer(runePathWriter())
                .build();
    }

    @Bean
    public Job importRuneJob(JobRepository jobRepository, Step runeStep) {
        return new JobBuilder("importRuneJob", jobRepository)
                .start(runeStep)
                .build();
    }
}
