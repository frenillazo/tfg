package rafa.tfg.domain.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.domain.model.recommendation.ItemCandidate;
import rafa.tfg.domain.model.recommendation.WeightProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del algoritmo TODIM (Tomada de Decisão Interativa Multicritério)
 * Algoritmo basado en teoría de prospectos con aversión a pérdidas
 */
@Service
@Slf4j
public class TODIMService {

    // Parámetro de aversión a pérdidas (típicamente 2.25 según estudios de Kahneman-Tversky)
    private static final double LOSS_AVERSION_LAMBDA = 2.25;

    /**
     * Aplica el algoritmo TODIM a los candidatos
     *
     * @param candidates Lista de candidatos con sus criterios (ya normalizados)
     * @param weights    Perfil de pesos para cada criterio
     * @return Lista de candidatos con scores TODIM calculados
     */
    public List<ItemCandidate> applyTODIM(List<ItemCandidate> candidates, WeightProfile weights) {
        log.info("Applying TODIM algorithm to {} candidates with loss aversion λ={}",
                candidates.size(), LOSS_AVERSION_LAMBDA);

        if (candidates.isEmpty()) {
            return candidates;
        }

        // Paso 1: Normalizar la matriz usando min-max
        Map<String, double[]> normalizedMatrix = normalizeMatrixMinMax(candidates);

        // Paso 2: Identificar el criterio de referencia (mayor peso)
        String referenceCriterion = findReferenceCriterion(weights);
        double referenceWeight = weights.getWeight(referenceCriterion);

        log.debug("Reference criterion: {} with weight: {}", referenceCriterion, referenceWeight);

        // Paso 3: Calcular matriz de dominancia global
        Map<Integer, Double> dominanceScores = new HashMap<>();

        for (int i = 0; i < candidates.size(); i++) {
            double totalDominance = 0.0;

            // Comparar alternativa i con todas las demás alternativas j
            for (int j = 0; j < candidates.size(); j++) {
                if (i != j) {
                    double pairwiseDominance = calculatePairwiseDominance(
                            i, j, normalizedMatrix, weights, referenceWeight);
                    totalDominance += pairwiseDominance;
                }
            }

            dominanceScores.put(i, totalDominance);
        }

        // Paso 4: Normalizar scores TODIM (0-1)
        double minDominance = dominanceScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

        double maxDominance = dominanceScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);

        double range = maxDominance - minDominance;

        for (int i = 0; i < candidates.size(); i++) {
            double rawScore = dominanceScores.get(i);
            double normalizedScore = (range > 0) ? ((rawScore - minDominance) / range) : 0.5;

            candidates.get(i).setTodimScore(normalizedScore);

            log.debug("Item {}: TODIM score = {}",
                    candidates.get(i).getItemName(), normalizedScore);
        }

        log.info("TODIM algorithm completed");

        return candidates;
    }

    /**
     * Normalización min-max de la matriz
     * Formula: normalized = (value - min) / (max - min)
     */
    private Map<String, double[]> normalizeMatrixMinMax(List<ItemCandidate> candidates) {
        Map<String, double[]> normalizedMatrix = new HashMap<>();

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            double[] values = new double[candidates.size()];

            // Encontrar min y max para este criterio
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (int i = 0; i < candidates.size(); i++) {
                double value = candidates.get(i).getCriteria().getOrDefault(key, 0.0);
                values[i] = value;
                min = Math.min(min, value);
                max = Math.max(max, value);
            }

            // Normalizar valores
            double range = max - min;
            double[] normalizedValues = new double[candidates.size()];

            for (int i = 0; i < values.length; i++) {
                normalizedValues[i] = (range > 0) ? ((values[i] - min) / range) : 0.5;
            }

            normalizedMatrix.put(key, normalizedValues);
        }

        return normalizedMatrix;
    }

    /**
     * Encuentra el criterio de referencia (el de mayor peso)
     */
    private String findReferenceCriterion(WeightProfile weights) {
        String referenceCriterion = ItemCandidate.Criterion.values()[0].getKey();
        double maxWeight = 0.0;

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            double weight = weights.getWeight(key);

            if (weight > maxWeight) {
                maxWeight = weight;
                referenceCriterion = key;
            }
        }

        return referenceCriterion;
    }

    /**
     * Calcula la dominancia entre dos alternativas (i vs j)
     * Formula TODIM con aversión a pérdidas
     */
    private double calculatePairwiseDominance(
            int i, int j,
            Map<String, double[]> normalizedMatrix,
            WeightProfile weights,
            double referenceWeight) {

        double dominance = 0.0;

        // Sumar dominancia para cada criterio
        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            double[] values = normalizedMatrix.get(key);

            double valueI = values[i];
            double valueJ = values[j];
            double weight = weights.getWeight(key);

            // Peso relativo del criterio
            double relativeWeight = weight / referenceWeight;

            // Diferencia entre las alternativas
            double difference = valueI - valueJ;

            // Aplicar función de dominancia con aversión a pérdidas
            double phi;

            if (difference > 0) {
                // Ganancia: phi = sqrt(relativeWeight * difference)
                phi = Math.sqrt(relativeWeight * difference);
            } else if (difference < 0) {
                // Pérdida: phi = -lambda * sqrt(relativeWeight * |difference|)
                phi = -LOSS_AVERSION_LAMBDA * Math.sqrt(relativeWeight * Math.abs(difference));
            } else {
                // Sin diferencia
                phi = 0.0;
            }

            dominance += phi;
        }

        return dominance;
    }
}
