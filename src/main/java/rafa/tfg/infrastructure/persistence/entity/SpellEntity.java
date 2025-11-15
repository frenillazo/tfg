package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para Spell (Habilidad de campeón)
 */
@Entity
@Table(name = "spells")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpellEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificación
    @Column(name = "spell_id", unique = true, nullable = false)
    private String spellId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "champion_id")
    private String championId;

    // Descripción
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tooltip", columnDefinition = "TEXT")
    private String tooltip;

    // Level tip info (almacenados como JSON en TEXT)
    @Column(name = "level_tip_labels", columnDefinition = "TEXT")
    private String levelTipLabels;

    @Column(name = "level_tip_effects", columnDefinition = "TEXT")
    private String levelTipEffects;

    // Valores base
    @Column(name = "max_rank")
    private Integer maxRank;

    @Column(name = "cooldown", columnDefinition = "TEXT")
    private String cooldown;

    @Column(name = "cooldown_burn")
    private String cooldownBurn;

    @Column(name = "cost", columnDefinition = "TEXT")
    private String cost;

    @Column(name = "cost_burn")
    private String costBurn;

    @Column(name = "cost_type")
    private String costType;

    // Efectos y variables (almacenados como JSON en TEXT)
    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;

    @Column(name = "effect_burn", columnDefinition = "TEXT")
    private String effectBurn;

    @Column(name = "vars", columnDefinition = "TEXT")
    private String vars;

    // Rango y ammo
    @Column(name = "range_values")
    private String range;

    @Column(name = "range_burn")
    private String rangeBurn;

    @Column(name = "max_ammo")
    private String maxAmmo;

    // Imagen
    @Column(name = "image_full")
    private String imageFull;

    @Column(name = "image_sprite")
    private String imageSprite;

    @Column(name = "image_group")
    private String imageGroup;

    // Recurso
    @Column(name = "resource")
    private String resource;

    // Datos adicionales (JSON serializado)
    @Column(name = "data_values", columnDefinition = "TEXT")
    private String dataValues;
}
