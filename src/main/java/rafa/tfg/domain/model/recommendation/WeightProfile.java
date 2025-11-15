package rafa.tfg.domain.model.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Pesos dinámicos para cada criterio basados en el perfil del campeón y contexto del juego
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightProfile {

    private Map<String, Double> weights;
    private Double totalWeight;

    /**
     * Normaliza los pesos para que sumen 1.0
     */
    public void normalize() {
        if (weights == null || weights.isEmpty()) {
            return;
        }

        totalWeight = weights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (totalWeight > 0) {
            weights.replaceAll((k, v) -> v / totalWeight);
            totalWeight = 1.0;
        }
    }

    /**
     * Obtiene el peso de un criterio específico
     */
    public Double getWeight(String criterion) {
        return weights.getOrDefault(criterion, 0.0);
    }

    /**
     * Establece el peso de un criterio
     */
    public void setWeight(String criterion, Double weight) {
        weights.put(criterion, weight);
    }

    /**
     * Incrementa el peso de un criterio
     */
    public void increaseWeight(String criterion, Double increment) {
        weights.put(criterion, weights.getOrDefault(criterion, 0.0) + increment);
    }
}
