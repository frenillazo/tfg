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
import rafa.tfg.domain.model.Champion;
import rafa.tfg.domain.service.ChampionService;
import rafa.tfg.infrastructure.batch.dto.ChampionDataWrapper;
import rafa.tfg.infrastructure.batch.dto.ChampionJsonDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuración de Spring Batch para cargar Champions desde JSON
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChampionBatchConfig {

    private final ChampionService championService;
    private final ObjectMapper objectMapper;

    @Bean
    public ItemReader<ChampionJsonDTO> championReader() {
        return new ItemReader<>() {
            private List<ChampionJsonDTO> champions;
            private int currentIndex = 0;

            @Override
            public ChampionJsonDTO read() throws IOException {
                if (champions == null) {
                    loadChampions();
                }

                if (currentIndex < champions.size()) {
                    return champions.get(currentIndex++);
                }

                return null; // End of data
            }

            private void loadChampions() throws IOException {
                ClassPathResource resource = new ClassPathResource("data/champion.json");
                ChampionDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), ChampionDataWrapper.class);
                champions = new ArrayList<>(wrapper.getData().values());
                log.info("Loaded {} champions from JSON", champions.size());
            }
        };
    }

    @Bean
    public ItemProcessor<ChampionJsonDTO, Champion> championProcessor() {
        return championDTO -> {
            log.debug("Processing champion: {}", championDTO.getName());

            return Champion.builder()
                    .championId(championDTO.getId())
                    .key(championDTO.getKey())
                    .name(championDTO.getName())
                    .title(championDTO.getTitle())
                    .blurb(championDTO.getBlurb())
                    .tags(championDTO.getTags())
                    .partype(championDTO.getPartype())
                    // Info stats
                    .attack(championDTO.getInfo() != null ? championDTO.getInfo().getAttack() : null)
                    .defense(championDTO.getInfo() != null ? championDTO.getInfo().getDefense() : null)
                    .magic(championDTO.getInfo() != null ? championDTO.getInfo().getMagic() : null)
                    .difficulty(championDTO.getInfo() != null ? championDTO.getInfo().getDifficulty() : null)
                    // Base stats
                    .hp(championDTO.getStats() != null ? championDTO.getStats().getHp() : null)
                    .hpPerLevel(championDTO.getStats() != null ? championDTO.getStats().getHpperlevel() : null)
                    .mp(championDTO.getStats() != null ? championDTO.getStats().getMp() : null)
                    .mpPerLevel(championDTO.getStats() != null ? championDTO.getStats().getMpperlevel() : null)
                    .moveSpeed(championDTO.getStats() != null ? championDTO.getStats().getMovespeed() : null)
                    .armor(championDTO.getStats() != null ? championDTO.getStats().getArmor() : null)
                    .armorPerLevel(championDTO.getStats() != null ? championDTO.getStats().getArmorperlevel() : null)
                    .spellBlock(championDTO.getStats() != null ? championDTO.getStats().getSpellblock() : null)
                    .spellBlockPerLevel(championDTO.getStats() != null ? championDTO.getStats().getSpellblockperlevel() : null)
                    .attackRange(championDTO.getStats() != null ? championDTO.getStats().getAttackrange() : null)
                    .hpRegen(championDTO.getStats() != null ? championDTO.getStats().getHpregen() : null)
                    .hpRegenPerLevel(championDTO.getStats() != null ? championDTO.getStats().getHpregenperlevel() : null)
                    .mpRegen(championDTO.getStats() != null ? championDTO.getStats().getMpregen() : null)
                    .mpRegenPerLevel(championDTO.getStats() != null ? championDTO.getStats().getMpregenperlevel() : null)
                    .crit(championDTO.getStats() != null ? championDTO.getStats().getCrit() : null)
                    .critPerLevel(championDTO.getStats() != null ? championDTO.getStats().getCritperlevel() : null)
                    .attackDamage(championDTO.getStats() != null ? championDTO.getStats().getAttackdamage() : null)
                    .attackDamagePerLevel(championDTO.getStats() != null ? championDTO.getStats().getAttackdamageperlevel() : null)
                    .attackSpeed(championDTO.getStats() != null ? championDTO.getStats().getAttackspeed() : null)
                    .attackSpeedPerLevel(championDTO.getStats() != null ? championDTO.getStats().getAttackspeedperlevel() : null)
                    .build();
        };
    }

    @Bean
    public ItemWriter<Champion> championWriter() {
        return champions -> {
            for (Champion champion : champions) {
                championService.create(champion);
                log.debug("Saved champion: {}", champion.getName());
            }
        };
    }

    @Bean
    public Step championStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("championStep", jobRepository)
                .<ChampionJsonDTO, Champion>chunk(10, transactionManager)
                .reader(championReader())
                .processor(championProcessor())
                .writer(championWriter())
                .build();
    }

    @Bean
    public Job importChampionJob(JobRepository jobRepository, Step championStep) {
        return new JobBuilder("importChampionJob", jobRepository)
                .start(championStep)
                .build();
    }
}
