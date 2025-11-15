package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para Champion
 */
@Entity
@Table(name = "champions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "champion_id", unique = true, nullable = false)
    private String championId;

    @Column(name = "key")
    private String key;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "blurb", columnDefinition = "TEXT")
    private String blurb;

    @Column(name = "tags")
    private String tags;  // Stored as comma-separated values

    @Column(name = "partype")
    private String partype;

    // Info stats
    @Column(name = "attack")
    private Integer attack;

    @Column(name = "defense")
    private Integer defense;

    @Column(name = "magic")
    private Integer magic;

    @Column(name = "difficulty")
    private Integer difficulty;

    // Base stats
    @Column(name = "hp")
    private Double hp;

    @Column(name = "hp_per_level")
    private Double hpPerLevel;

    @Column(name = "mp")
    private Double mp;

    @Column(name = "mp_per_level")
    private Double mpPerLevel;

    @Column(name = "move_speed")
    private Double moveSpeed;

    @Column(name = "armor")
    private Double armor;

    @Column(name = "armor_per_level")
    private Double armorPerLevel;

    @Column(name = "spell_block")
    private Double spellBlock;

    @Column(name = "spell_block_per_level")
    private Double spellBlockPerLevel;

    @Column(name = "attack_range")
    private Double attackRange;

    @Column(name = "hp_regen")
    private Double hpRegen;

    @Column(name = "hp_regen_per_level")
    private Double hpRegenPerLevel;

    @Column(name = "mp_regen")
    private Double mpRegen;

    @Column(name = "mp_regen_per_level")
    private Double mpRegenPerLevel;

    @Column(name = "crit")
    private Double crit;

    @Column(name = "crit_per_level")
    private Double critPerLevel;

    @Column(name = "attack_damage")
    private Double attackDamage;

    @Column(name = "attack_damage_per_level")
    private Double attackDamagePerLevel;

    @Column(name = "attack_speed")
    private Double attackSpeed;

    @Column(name = "attack_speed_per_level")
    private Double attackSpeedPerLevel;
}
