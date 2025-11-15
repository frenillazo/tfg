package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad de dominio para Item (Objeto del juego)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;
    private String itemId;       // ID del item como string
    private String name;
    private String description;
    private String colloq;
    private String plaintext;
    private String group;

    // Gold info
    private Integer goldBase;
    private Integer goldTotal;
    private Integer goldSell;
    private Boolean purchasable;

    // Item properties
    private Boolean consumed;
    private Integer stacks;
    private Integer depth;
    private Boolean inStore;
    private Boolean hideFromAll;
    private String requiredChampion;
    private String requiredAlly;

    // Build path
    private List<String> from;
    private List<String> into;

    // Stats - principales stats del item
    private Double flatHPPoolMod;
    private Double flatMPPoolMod;
    private Double percentHPPoolMod;
    private Double percentMPPoolMod;
    private Double flatHPRegenMod;
    private Double percentHPRegenMod;
    private Double flatMPRegenMod;
    private Double percentMPRegenMod;
    private Double flatArmorMod;
    private Double percentArmorMod;
    private Double flatAttackSpeedMod;
    private Double percentAttackSpeedMod;
    private Double flatCritChanceMod;
    private Double flatPhysicalDamageMod;
    private Double flatMagicDamageMod;
    private Double percentLifeStealMod;
    private Double percentSpellVampMod;
}
