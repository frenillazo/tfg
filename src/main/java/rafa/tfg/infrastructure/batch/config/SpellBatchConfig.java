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
import rafa.tfg.infrastructure.batch.dto.ChampionDataWrapper;
import rafa.tfg.infrastructure.batch.dto.ChampionJsonDTO;
import rafa.tfg.infrastructure.batch.dto.SpellJsonDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuración de Spring Batch para cargar Spells (habilidades) desde championFull.json
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpellBatchConfig {

    private final SpellService spellService;
    private final ObjectMapper objectMapper;

    /**
     * Clase auxiliar para asociar un spell con su championId
     */
    public static class SpellWithChampion {
        public SpellJsonDTO spell;
        public String championId;

        public SpellWithChampion(SpellJsonDTO spell, String championId) {
            this.spell = spell;
            this.championId = championId;
        }
    }

    @Bean
    public ItemReader<SpellWithChampion> spellReader() {
        return new ItemReader<>() {
            private List<SpellWithChampion> spells;
            private int currentIndex = 0;

            @Override
            public SpellWithChampion read() throws IOException {
                if (spells == null) {
                    loadSpells();
                }

                if (currentIndex < spells.size()) {
                    return spells.get(currentIndex++);
                }

                return null; // End of data
            }

            private void loadSpells() throws IOException {
                ClassPathResource resource = new ClassPathResource("data/championFull.json");
                ChampionDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), ChampionDataWrapper.class);

                spells = new ArrayList<>();

                // Iterar sobre todos los campeones y extraer sus habilidades
                for (Map.Entry<String, ChampionJsonDTO> entry : wrapper.getData().entrySet()) {
                    String championId = entry.getKey();
                    ChampionJsonDTO champion = entry.getValue();

                    if (champion.getSpells() != null) {
                        for (SpellJsonDTO spell : champion.getSpells()) {
                            spells.add(new SpellWithChampion(spell, championId));
                        }
                    }
                }

                log.info("Loaded {} spells from {} champions", spells.size(), wrapper.getData().size());
            }
        };
    }

    @Bean
    public ItemProcessor<SpellWithChampion, Spell> spellProcessor() {
        return spellWithChampion -> {
            SpellJsonDTO spellDTO = spellWithChampion.spell;
            String championId = spellWithChampion.championId;

            log.debug("Processing spell: {} for champion: {}", spellDTO.getName(), championId);

            try {
                return Spell.builder()
                        .spellId(spellDTO.getId())
                        .name(spellDTO.getName())
                        .championId(championId)
                        .description(spellDTO.getDescription())
                        .tooltip(spellDTO.getTooltip())
                        // Level tip
                        .levelTipLabels(spellDTO.getLevelTip() != null ? spellDTO.getLevelTip().getLabel() : null)
                        .levelTipEffects(spellDTO.getLevelTip() != null ? spellDTO.getLevelTip().getEffect() : null)
                        // Valores base
                        .maxRank(spellDTO.getMaxRank())
                        .cooldown(spellDTO.getCooldown())
                        .cooldownBurn(spellDTO.getCooldownBurn())
                        .cost(spellDTO.getCost())
                        .costBurn(spellDTO.getCostBurn())
                        .costType(spellDTO.getCostType())
                        // Efectos y variables
                        .effect(spellDTO.getEffect())
                        .effectBurn(spellDTO.getEffectBurn())
                        .vars(spellDTO.getVars() != null ? objectMapper.writeValueAsString(spellDTO.getVars()) : null)
                        // Rango y ammo
                        .range(spellDTO.getRange())
                        .rangeBurn(spellDTO.getRangeBurn())
                        .maxAmmo(spellDTO.getMaxAmmo())
                        // Imagen
                        .imageFull(spellDTO.getImage() != null ? spellDTO.getImage().getFull() : null)
                        .imageSprite(spellDTO.getImage() != null ? spellDTO.getImage().getSprite() : null)
                        .imageGroup(spellDTO.getImage() != null ? spellDTO.getImage().getGroup() : null)
                        // Recurso
                        .resource(spellDTO.getResource())
                        // Datos adicionales
                        .dataValues(spellDTO.getDataValues() != null ? objectMapper.writeValueAsString(spellDTO.getDataValues()) : null)
                        .build();
            } catch (Exception e) {
                log.error("Error processing spell: {} for champion: {}", spellDTO.getName(), championId, e);
                throw new RuntimeException("Error processing spell", e);
            }
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
                .<SpellWithChampion, Spell>chunk(10, transactionManager)
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
