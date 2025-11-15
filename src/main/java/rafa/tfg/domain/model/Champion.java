package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad de dominio para Champion (Campeón)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Champion {

    private Long id;
    private String championId;  // ID string del campeón (ej: "Aatrox")
    private String key;          // Key numérica del campeón
    private String name;
    private String title;
    private String blurb;
    private List<String> tags;
    private String partype;

    // Info stats
    private Integer attack;
    private Integer defense;
    private Integer magic;
    private Integer difficulty;

    // Base stats
    private Double hp;
    private Double hpPerLevel;
    private Double mp;
    private Double mpPerLevel;
    private Double moveSpeed;
    private Double armor;
    private Double armorPerLevel;
    private Double spellBlock;
    private Double spellBlockPerLevel;
    private Double attackRange;
    private Double hpRegen;
    private Double hpRegenPerLevel;
    private Double mpRegen;
    private Double mpRegenPerLevel;
    private Double crit;
    private Double critPerLevel;
    private Double attackDamage;
    private Double attackDamagePerLevel;
    private Double attackSpeed;
    private Double attackSpeedPerLevel;
}
