package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad de dominio para RunePath (Camino de runas: Precision, Domination, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunePath {

    private Long id;
    private Integer pathId;      // ID del path en el juego
    private String key;
    private String icon;
    private String name;

    // Las runas pertenecientes a este path
    private List<Rune> runes;
}
