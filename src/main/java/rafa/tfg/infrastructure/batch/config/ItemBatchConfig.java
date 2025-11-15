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
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.service.ItemService;
import rafa.tfg.infrastructure.batch.dto.ItemDataWrapper;
import rafa.tfg.infrastructure.batch.dto.ItemJsonDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuración de Spring Batch para cargar Items desde JSON
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemBatchConfig {

    private final ItemService itemService;
    private final ObjectMapper objectMapper;

    @Bean
    public ItemReader<Map.Entry<String, ItemJsonDTO>> itemReader() {
        return new ItemReader<>() {
            private List<Map.Entry<String, ItemJsonDTO>> items;
            private int currentIndex = 0;

            @Override
            public Map.Entry<String, ItemJsonDTO> read() throws IOException {
                if (items == null) {
                    loadItems();
                }

                if (currentIndex < items.size()) {
                    return items.get(currentIndex++);
                }

                return null; // End of data
            }

            private void loadItems() throws IOException {
                ClassPathResource resource = new ClassPathResource("data/item.json");
                ItemDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), ItemDataWrapper.class);
                items = new ArrayList<>(wrapper.getData().entrySet());
                log.info("Loaded {} items from JSON", items.size());
            }
        };
    }

    @Bean
    public ItemProcessor<Map.Entry<String, ItemJsonDTO>, Item> itemProcessor() {
        return entry -> {
            String itemId = entry.getKey();
            ItemJsonDTO itemDTO = entry.getValue();
            log.debug("Processing item: {}", itemDTO.getName());

            return Item.builder()
                    .itemId(itemId)
                    .name(itemDTO.getName())
                    .description(itemDTO.getDescription())
                    .colloq(itemDTO.getColloq())
                    .plaintext(itemDTO.getPlaintext())
                    .group(itemDTO.getGroup())
                    // Gold info
                    .goldBase(itemDTO.getGold() != null ? itemDTO.getGold().getBase() : null)
                    .goldTotal(itemDTO.getGold() != null ? itemDTO.getGold().getTotal() : null)
                    .goldSell(itemDTO.getGold() != null ? itemDTO.getGold().getSell() : null)
                    .purchasable(itemDTO.getGold() != null ? itemDTO.getGold().getPurchasable() : null)
                    // Item properties
                    .consumed(itemDTO.getConsumed())
                    .stacks(itemDTO.getStacks())
                    .depth(itemDTO.getDepth())
                    .inStore(itemDTO.getInStore())
                    .hideFromAll(itemDTO.getHideFromAll())
                    .requiredChampion(itemDTO.getRequiredChampion())
                    .requiredAlly(itemDTO.getRequiredAlly())
                    // Build path
                    .from(itemDTO.getFrom())
                    .into(itemDTO.getInto())
                    // Stats
                    .flatHPPoolMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatHPPoolMod() : null)
                    .flatMPPoolMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatMPPoolMod() : null)
                    .percentHPPoolMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentHPPoolMod() : null)
                    .percentMPPoolMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentMPPoolMod() : null)
                    .flatHPRegenMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatHPRegenMod() : null)
                    .percentHPRegenMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentHPRegenMod() : null)
                    .flatMPRegenMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatMPRegenMod() : null)
                    .percentMPRegenMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentMPRegenMod() : null)
                    .flatArmorMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatArmorMod() : null)
                    .percentArmorMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentArmorMod() : null)
                    .flatAttackSpeedMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatAttackSpeedMod() : null)
                    .percentAttackSpeedMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentAttackSpeedMod() : null)
                    .flatCritChanceMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatCritChanceMod() : null)
                    .flatPhysicalDamageMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatPhysicalDamageMod() : null)
                    .flatMagicDamageMod(itemDTO.getStats() != null ? itemDTO.getStats().getFlatMagicDamageMod() : null)
                    .percentLifeStealMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentLifeStealMod() : null)
                    .percentSpellVampMod(itemDTO.getStats() != null ? itemDTO.getStats().getPercentSpellVampMod() : null)
                    .build();
        };
    }

    @Bean
    public ItemWriter<Item> itemWriter() {
        return items -> {
            for (Item item : items) {
                itemService.create(item);
                log.debug("Saved item: {}", item.getName());
            }
        };
    }

    @Bean
    public Step itemStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("itemStep", jobRepository)
                .<Map.Entry<String, ItemJsonDTO>, Item>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job importItemJob(JobRepository jobRepository, Step itemStep) {
        return new JobBuilder("importItemJob", jobRepository)
                .start(itemStep)
                .build();
    }
}
