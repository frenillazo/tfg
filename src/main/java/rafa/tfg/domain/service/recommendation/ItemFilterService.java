package rafa.tfg.domain.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.application.dto.recommendation.GameStateRequestDTO;
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.model.recommendation.ChampionProfile;
import rafa.tfg.domain.port.ItemRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para filtrar items candidatos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemFilterService {

    private final ItemRepository itemRepository;

    /**
     * Filtra items candidatos según criterios de relevancia
     */
    public List<Item> filterCandidateItems(
            ChampionProfile championProfile,
            List<GameStateRequestDTO.ItemDTO> currentItems,
            Double currentGold) {

        log.info("Filtering candidate items for champion profile: {}", championProfile.getScalingType());

        // Obtener todos los items de la BD
        List<Item> allItems = itemRepository.findAll();

        // Obtener IDs de items que ya tiene el jugador
        Set<String> ownedItemIds = currentItems.stream()
                .map(item -> String.valueOf(item.getItemID()))
                .collect(Collectors.toSet());

        log.debug("Player currently owns {} items", ownedItemIds.size());

        // Filtrar items
        List<Item> candidateItems = allItems.stream()
                .filter(item -> isCompleteItem(item))
                .filter(item -> isPurchasableItem(item))
                .filter(item -> isRelevantForChampion(item, championProfile))
                .filter(item -> !ownedItemIds.contains(item.getItemId()))
                .collect(Collectors.toList());

        log.info("Found {} candidate items after filtering", candidateItems.size());

        return candidateItems;
    }

    /**
     * Verifica si es un item completo (no componente)
     */
    private boolean isCompleteItem(Item item) {
        // Un item es completo si:
        // 1. No tiene items en los que se convierte (campo 'into' vacío)
        // 2. O tiene depth >= 3 (usualmente items legendarios/míticos)

        String into = item.getInto() != null ? String.join(",", item.getInto()) : null;
        boolean hasNoUpgrade = (into == null || into.trim().isEmpty() || into.equals("[]"));

        Integer depth = item.getDepth();
        boolean isHighDepth = (depth != null && depth >= 3);

        return hasNoUpgrade || isHighDepth;
    }

    /**
     * Verifica si el item es comprable en la tienda
     */
    private boolean isPurchasableItem(Item item) {
        Boolean inStore = item.getInStore();
        Boolean purchasable = item.getPurchasable();

        return Boolean.TRUE.equals(inStore) && Boolean.TRUE.equals(purchasable);
    }

    /**
     * Verifica si el item es relevante para el perfil del campeón
     */
    private boolean isRelevantForChampion(Item item, ChampionProfile championProfile) {
        // Filtrar items según el tipo de escalado del campeón

        switch (championProfile.getScalingType()) {
            case AD_FOCUSED:
                return hasAdStats(item) || hasPhysicalDamageStats(item);

            case AP_FOCUSED:
                return hasApStats(item) || hasMagicDamageStats(item);

            case TANK:
                return hasTankStats(item);

            case MIXED:
                // Aceptar items con AD, AP o hybrid
                return hasAdStats(item) || hasApStats(item) || hasHybridStats(item);

            case UTILITY:
                // Aceptar items con CDR, MS, utility stats
                return hasUtilityStats(item);

            default:
                return true; // Aceptar cualquier item por defecto
        }
    }

    private boolean hasAdStats(Item item) {
        return (item.getFlatPhysicalDamageMod() != null && item.getFlatPhysicalDamageMod() > 0)
                || (item.getPercentAttackSpeedMod() != null && item.getPercentAttackSpeedMod() > 0)
                || (item.getFlatCritChanceMod() != null && item.getFlatCritChanceMod() > 0);
    }

    private boolean hasApStats(Item item) {
        return (item.getFlatMagicDamageMod() != null && item.getFlatMagicDamageMod() > 0);
    }

    private boolean hasPhysicalDamageStats(Item item) {
        return hasAdStats(item)
                || (item.getPercentArmorMod() != null && item.getPercentArmorMod() > 0)
                || (item.getPercentLifeStealMod() != null && item.getPercentLifeStealMod() > 0);
    }

    private boolean hasMagicDamageStats(Item item) {
        return hasApStats(item)
                || (item.getPercentMPPoolMod() != null && item.getPercentMPPoolMod() > 0);
    }

    private boolean hasTankStats(Item item) {
        return (item.getFlatHPPoolMod() != null && item.getFlatHPPoolMod() > 0)
                || (item.getFlatArmorMod() != null && item.getFlatArmorMod() > 0)
                || (item.getFlatSpellBlockMod() != null && item.getFlatSpellBlockMod() > 0);
    }

    private boolean hasHybridStats(Item item) {
        boolean hasAd = hasAdStats(item);
        boolean hasAp = hasApStats(item);
        boolean hasTank = hasTankStats(item);

        return (hasAd && hasAp) || (hasAd && hasTank) || (hasAp && hasTank);
    }

    private boolean hasUtilityStats(Item item) {
        return (item.getFlatMovementSpeedMod() != null && item.getFlatMovementSpeedMod() > 0)
                || (item.getFlatMovementSpeedMod() != null && item.getFlatMovementSpeedMod() > 0);
    }
}
