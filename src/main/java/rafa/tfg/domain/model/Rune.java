package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Rune (Runa individual)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rune {

    private Long id;
    private Integer runeId;      // ID de la runa en el juego
    private String key;
    private String icon;
    private String name;
    private String shortDesc;
    private String longDesc;

    // Relación con el slot y path
    private Long runePathId;
    private Integer slotPosition;
}
