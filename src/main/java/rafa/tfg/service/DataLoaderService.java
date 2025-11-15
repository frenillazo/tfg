package rafa.tfg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.model.Champion;
import rafa.tfg.model.Item;
import rafa.tfg.repository.ChampionRepository;
import rafa.tfg.repository.ItemRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoaderService {

    private final ChampionRepository championRepository;
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void loadChampions() throws IOException {
        log.info("Loading champions from JSON...");

        ClassPathResource resource = new ClassPathResource("data/champion.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        JsonNode dataNode = rootNode.get("data");

        List<Champion> champions = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode championNode = entry.getValue();

            Champion champion = new Champion();
            champion.setId(getTextOrNull(championNode, "id"));
            champion.setKey(getTextOrNull(championNode, "key"));
            champion.setName(getTextOrNull(championNode, "name"));
            champion.setTitle(getTextOrNull(championNode, "title"));
            champion.setBlurb(getTextOrNull(championNode, "blurb"));
            champion.setVersion(getTextOrNull(championNode, "version"));
            champion.setPartype(getTextOrNull(championNode, "partype"));

            // Info
            JsonNode infoNode = championNode.get("info");
            if (infoNode != null) {
                champion.setAttackInfo(getIntOrNull(infoNode, "attack"));
                champion.setDefenseInfo(getIntOrNull(infoNode, "defense"));
                champion.setMagicInfo(getIntOrNull(infoNode, "magic"));
                champion.setDifficultyInfo(getIntOrNull(infoNode, "difficulty"));
            }

            // Tags
            JsonNode tagsNode = championNode.get("tags");
            if (tagsNode != null && tagsNode.isArray()) {
                List<String> tagList = new ArrayList<>();
                tagsNode.forEach(tag -> tagList.add(tag.asText()));
                champion.setTags(String.join(",", tagList));
            }

            // Stats
            JsonNode statsNode = championNode.get("stats");
            if (statsNode != null) {
                champion.setHp(getDoubleOrNull(statsNode, "hp"));
                champion.setHpperlevel(getDoubleOrNull(statsNode, "hpperlevel"));
                champion.setMp(getDoubleOrNull(statsNode, "mp"));
                champion.setMpperlevel(getDoubleOrNull(statsNode, "mpperlevel"));
                champion.setMovespeed(getDoubleOrNull(statsNode, "movespeed"));
                champion.setArmor(getDoubleOrNull(statsNode, "armor"));
                champion.setArmorperlevel(getDoubleOrNull(statsNode, "armorperlevel"));
                champion.setSpellblock(getDoubleOrNull(statsNode, "spellblock"));
                champion.setSpellblockperlevel(getDoubleOrNull(statsNode, "spellblockperlevel"));
                champion.setAttackrange(getIntOrNull(statsNode, "attackrange"));
                champion.setHpregen(getDoubleOrNull(statsNode, "hpregen"));
                champion.setHpregenperlevel(getDoubleOrNull(statsNode, "hpregenperlevel"));
                champion.setMpregen(getDoubleOrNull(statsNode, "mpregen"));
                champion.setMpregenperlevel(getDoubleOrNull(statsNode, "mpregenperlevel"));
                champion.setAttackdamage(getDoubleOrNull(statsNode, "attackdamage"));
                champion.setAttackdamageperlevel(getDoubleOrNull(statsNode, "attackdamageperlevel"));
                champion.setAttackspeed(getDoubleOrNull(statsNode, "attackspeed"));
                champion.setAttackspeedperlevel(getDoubleOrNull(statsNode, "attackspeedperlevel"));
            }

            champions.add(champion);
        }

        championRepository.saveAll(champions);
        log.info("Loaded {} champions", champions.size());
    }

    @Transactional
    public void loadItems() throws IOException {
        log.info("Loading items from JSON...");

        ClassPathResource resource = new ClassPathResource("data/item.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        JsonNode dataNode = rootNode.get("data");

        List<Item> items = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String itemId = entry.getKey();
            JsonNode itemNode = entry.getValue();

            Item item = new Item();
            item.setId(itemId);
            item.setName(getTextOrNull(itemNode, "name"));
            item.setDescription(getTextOrNull(itemNode, "description"));
            item.setPlaintext(getTextOrNull(itemNode, "plaintext"));
            item.setColloq(getTextOrNull(itemNode, "colloq"));

            // Gold
            JsonNode goldNode = itemNode.get("gold");
            if (goldNode != null) {
                item.setGoldBase(getIntOrNull(goldNode, "base"));
                item.setGoldTotal(getIntOrNull(goldNode, "total"));
                item.setGoldSell(getIntOrNull(goldNode, "sell"));
                item.setPurchasable(getBooleanOrNull(goldNode, "purchasable"));
            }

            // Tags
            JsonNode tagsNode = itemNode.get("tags");
            if (tagsNode != null && tagsNode.isArray()) {
                List<String> tagList = new ArrayList<>();
                tagsNode.forEach(tag -> tagList.add(tag.asText()));
                item.setTags(String.join(",", tagList));
            }

            // Recipe
            JsonNode fromNode = itemNode.get("from");
            if (fromNode != null && fromNode.isArray()) {
                List<String> fromList = new ArrayList<>();
                fromNode.forEach(f -> fromList.add(f.asText()));
                item.setBuildsFrom(String.join(",", fromList));
            }

            JsonNode intoNode = itemNode.get("into");
            if (intoNode != null && intoNode.isArray()) {
                List<String> intoList = new ArrayList<>();
                intoNode.forEach(i -> intoList.add(i.asText()));
                item.setBuildsInto(String.join(",", intoList));
            }

            // Stats
            JsonNode statsNode = itemNode.get("stats");
            if (statsNode != null) {
                item.setFlatHPPoolMod(getDoubleOrNull(statsNode, "FlatHPPoolMod"));
                item.setFlatMPPoolMod(getDoubleOrNull(statsNode, "FlatMPPoolMod"));
                item.setFlatArmorMod(getDoubleOrNull(statsNode, "FlatArmorMod"));
                item.setFlatSpellBlockMod(getDoubleOrNull(statsNode, "FlatSpellBlockMod"));
                item.setFlatPhysicalDamageMod(getDoubleOrNull(statsNode, "FlatPhysicalDamageMod"));
                item.setFlatMagicDamageMod(getDoubleOrNull(statsNode, "FlatMagicDamageMod"));
                item.setPercentAttackSpeedMod(getDoubleOrNull(statsNode, "PercentAttackSpeedMod"));
                item.setPercentLifeStealMod(getDoubleOrNull(statsNode, "PercentLifeStealMod"));
                item.setFlatMovementSpeedMod(getDoubleOrNull(statsNode, "FlatMovementSpeedMod"));
                item.setFlatCritChanceMod(getDoubleOrNull(statsNode, "FlatCritChanceMod"));
            }

            item.setDepth(getIntOrNull(itemNode, "depth"));

            items.add(item);
        }

        itemRepository.saveAll(items);
        log.info("Loaded {} items", items.size());
    }

    // Helper methods
    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private Integer getIntOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asInt() : null;
    }

    private Double getDoubleOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asDouble() : null;
    }

    private Boolean getBooleanOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asBoolean() : null;
    }
}
