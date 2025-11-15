package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para Item
 */
@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", unique = true, nullable = false)
    private String itemId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "colloq")
    private String colloq;

    @Column(name = "plaintext", columnDefinition = "TEXT")
    private String plaintext;

    @Column(name = "group_name")
    private String group;

    // Gold info
    @Column(name = "gold_base")
    private Integer goldBase;

    @Column(name = "gold_total")
    private Integer goldTotal;

    @Column(name = "gold_sell")
    private Integer goldSell;

    @Column(name = "purchasable")
    private Boolean purchasable;

    // Item properties
    @Column(name = "consumed")
    private Boolean consumed;

    @Column(name = "stacks")
    private Integer stacks;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "in_store")
    private Boolean inStore;

    @Column(name = "hide_from_all")
    private Boolean hideFromAll;

    @Column(name = "required_champion")
    private String requiredChampion;

    @Column(name = "required_ally")
    private String requiredAlly;

    // Build path - stored as comma-separated values
    @Column(name = "from_items")
    private String from;

    @Column(name = "into_items")
    private String into;

    // Stats
    @Column(name = "flat_hp_pool_mod")
    private Double flatHPPoolMod;

    @Column(name = "flat_mp_pool_mod")
    private Double flatMPPoolMod;

    @Column(name = "percent_hp_pool_mod")
    private Double percentHPPoolMod;

    @Column(name = "percent_mp_pool_mod")
    private Double percentMPPoolMod;

    @Column(name = "flat_hp_regen_mod")
    private Double flatHPRegenMod;

    @Column(name = "percent_hp_regen_mod")
    private Double percentHPRegenMod;

    @Column(name = "flat_mp_regen_mod")
    private Double flatMPRegenMod;

    @Column(name = "percent_mp_regen_mod")
    private Double percentMPRegenMod;

    @Column(name = "flat_armor_mod")
    private Double flatArmorMod;

    @Column(name = "percent_armor_mod")
    private Double percentArmorMod;

    @Column(name = "flat_attack_speed_mod")
    private Double flatAttackSpeedMod;

    @Column(name = "percent_attack_speed_mod")
    private Double percentAttackSpeedMod;

    @Column(name = "flat_crit_chance_mod")
    private Double flatCritChanceMod;

    @Column(name = "flat_physical_damage_mod")
    private Double flatPhysicalDamageMod;

    @Column(name = "flat_magic_damage_mod")
    private Double flatMagicDamageMod;

    @Column(name = "percent_life_steal_mod")
    private Double percentLifeStealMod;

    @Column(name = "percent_spell_vamp_mod")
    private Double percentSpellVampMod;

    @Column(name = "flat_movement_speed_mod")
    private Double flatMovementSpeedMod;

    @Column(name = "flat_spell_block_mod")
    private Double flatSpellBlockMod;
}
