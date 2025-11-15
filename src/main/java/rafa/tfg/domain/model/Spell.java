package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Entidad de dominio para Spell (Habilidad de campeón)
 * Representa cada una de las habilidades (Q, W, E, R) de los campeones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spell {

    private Long id;

    // Identificación
    private String spellId;          // ID único del spell (ej: "AatroxQ")
    private String name;             // Nombre de la habilidad
    private String championId;       // ID del campeón al que pertenece

    // Descripción
    private String description;      // Descripción corta
    private String tooltip;          // Tooltip detallado con variables

    // Level tip info
    private List<String> levelTipLabels;    // Labels para los tips de nivel
    private List<String> levelTipEffects;   // Efectos para los tips de nivel

    // Valores base
    private Integer maxRank;         // Rango máximo de la habilidad
    private List<Double> cooldown;   // Cooldown por nivel
    private String cooldownBurn;     // Cooldown como string
    private List<Integer> cost;      // Costo por nivel
    private String costBurn;         // Costo como string
    private String costType;         // Tipo de costo (Mana, Energy, etc.)

    // Efectos y variables
    private List<List<Double>> effect;      // Matriz de efectos
    private List<String> effectBurn;        // Efectos como strings
    private String vars;                     // Variables (JSON serializado)

    // Rango y ammo
    private List<Long> range;     // Rango por nivel
    private String rangeBurn;        // Rango como string
    private String maxAmmo;          // Máximo de ammo

    // Imagen
    private String imageFull;        // Nombre de archivo de imagen
    private String imageSprite;      // Sprite sheet
    private String imageGroup;       // Grupo de imagen

    // Recurso
    private String resource;         // Recurso mostrado

    // Datos adicionales
    private String dataValues;       // Valores de datos (JSON serializado)
}
