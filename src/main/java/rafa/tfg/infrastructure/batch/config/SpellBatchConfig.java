package rafa.tfg.infrastructure.batch.config;

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
import rafa.tfg.domain.model.Spell;
import rafa.tfg.domain.service.SpellService;
import rafa.tfg.infrastructure.batch.dto.SpellDataWrapper;
import rafa.tfg.infrastructure.batch.dto.SpellJsonDTO;

import java.io.IOException;
import java.util.List;

/**
 * Configuración de Spring Batch para cargar Spells desde JSON
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpellBatchConfig {

    private final SpellService spellService;
    private final ObjectMapper objectMapper;

    @Bean
    public ItemReader<SpellJsonDTO> spellReader() {
        return new ItemReader<>() {
            private List<SpellJsonDTO> spells;
            private int currentIndex = 0;

            @Override
            public SpellJsonDTO read() throws IOException {
                if (spells == null) {
                    loadSpells();
                }

                if (currentIndex < spells.size()) {
                    return spells.get(currentIndex++);
                }

                return null; // End of data
            }

            private void loadSpells() throws IOException {
                ClassPathResource resource = new ClassPathResource("data/spellbuffs.json");
                SpellDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), SpellDataWrapper.class);
                spells = wrapper.getSpellBuffs();
                log.info("Loaded {} spells from JSON", spells.size());
            }
        };
    }

    @Bean
    public ItemProcessor<SpellJsonDTO, Spell> spellProcessor() {
        return spellDTO -> {
            log.debug("Processing spell: {}", spellDTO.getName());

            return Spell.builder()
                    .spellId(spellDTO.getId())
                    .name(spellDTO.getName())
                    .build();
        };
    }

    @Bean
    public ItemWriter<Spell> spellWriter() {
        return spells -> {
            for (Spell spell : spells) {
                spellService.create(spell);
                log.debug("Saved spell: {}", spell.getName());
            }
        };
    }

    @Bean
    public Step spellStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("spellStep", jobRepository)
                .<SpellJsonDTO, Spell>chunk(10, transactionManager)
                .reader(spellReader())
                .processor(spellProcessor())
                .writer(spellWriter())
                .build();
    }

    @Bean
    public Job importSpellJob(JobRepository jobRepository, Step spellStep) {
        return new JobBuilder("importSpellJob", jobRepository)
                .start(spellStep)
                .build();
    }
}
