package rafa.tfg.domain.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.domain.model.recommendation.ItemCandidate;
import rafa.tfg.domain.model.recommendation.WeightProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del algoritmo TOPSIS (Technique for Order Preference by Similarity to Ideal Solution)
 */
@Service
@Slf4j
public class TOPSISService {

    /**
     * Aplica el algoritmo TOPSIS a los candidatos
     *
     * @param candidates Lista de candidatos con sus criterios
     * @param weights    Perfil de pesos para cada criterio
     * @return Lista de candidatos con scores TOPSIS calculados
     */
    public List<ItemCandidate> applyTOPSIS(List<ItemCandidate> candidates, WeightProfile weights) {
        log.info("Applying TOPSIS algorithm to {} candidates", candidates.size());

        if (candidates.isEmpty()) {
            return candidates;
        }

        // Paso 1: Normalizar la matriz (normalización vectorial)
        normalizeMatrix(candidates);

        // Paso 2: Aplicar pesos a la matriz normalizada
        applyWeights(candidates, weights);

        // Paso 3: Determinar soluciones ideales (PIS y NIS)
        Map<String, Double> idealPositive = calculateIdealPositiveSolution(candidates);
        Map<String, Double> idealNegative = calculateIdealNegativeSolution(candidates);

        // Paso 4 y 5: Calcular distancias y scores de proximidad
        for (ItemCandidate candidate : candidates) {
            double distanceToPositive = calculateEuclideanDistance(candidate, idealPositive);
            double distanceToNegative = calculateEuclideanDistance(candidate, idealNegative);

            // Score de proximidad relativa
            double topsisScore = distanceToNegative / (distanceToPositive + distanceToNegative);

            candidate.setTopsisScore(topsisScore);

            log.debug("Item {}: TOPSIS score = {}", candidate.getItemName(), topsisScore);
        }

        log.info("TOPSIS algorithm completed");

        return candidates;
    }

    /**
     * Paso 1: Normalización vectorial de la matriz
     * Formula: r_ij = x_ij / sqrt(sum(x_ij^2))
     */
    private void normalizeMatrix(List<ItemCandidate> candidates) {
        // Calcular la raíz de la suma de cuadrados para cada criterio
        Map<String, Double> sumOfSquares = new HashMap<>();

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            double sum = 0.0;

            for (ItemCandidate candidate : candidates) {
                double value = candidate.getCriteria().getOrDefault(key, 0.0);
                sum += value * value;
            }

            sumOfSquares.put(key, Math.sqrt(sum));
        }

        // Normalizar cada valor
        for (ItemCandidate candidate : candidates) {
            Map<String, Double> normalizedCriteria = new HashMap<>();

            for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
                String key = criterion.getKey();
                double value = candidate.getCriteria().getOrDefault(key, 0.0);
                double denominator = sumOfSquares.get(key);

                double normalized = (denominator > 0) ? (value / denominator) : 0.0;
                normalizedCriteria.put(key, normalized);
            }

            // Guardar valores normalizados (los usaremos en el cálculo ponderado)
            candidate.getCriteria().putAll(normalizedCriteria);
        }
    }

    /**
     * Paso 2: Aplicar pesos a la matriz normalizada
     * Formula: v_ij = w_j * r_ij
     */
    private void applyWeights(List<ItemCandidate> candidates, WeightProfile weights) {
        for (ItemCandidate candidate : candidates) {
            Map<String, Double> weightedCriteria = new HashMap<>();

            for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
                String key = criterion.getKey();
                double normalized = candidate.getCriteria().getOrDefault(key, 0.0);
                double weight = weights.getWeight(key);

                double weighted = normalized * weight;
                weightedCriteria.put(key, weighted);
            }

            candidate.getCriteria().putAll(weightedCriteria);
        }
    }

    /**
     * Paso 3a: Calcular solución ideal positiva (PIS)
     * PIS: Máximo valor para criterios de beneficio
     */
    private Map<String, Double> calculateIdealPositiveSolution(List<ItemCandidate> candidates) {
        Map<String, Double> idealPositive = new HashMap<>();

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();

            // Todos los criterios son de beneficio (mayor es mejor)
            double maxValue = candidates.stream()
                    .mapToDouble(c -> c.getCriteria().getOrDefault(key, 0.0))
                    .max()
                    .orElse(0.0);

            idealPositive.put(key, maxValue);
        }

        return idealPositive;
    }

    /**
     * Paso 3b: Calcular solución ideal negativa (NIS)
     * NIS: Mínimo valor para criterios de beneficio
     */
    private Map<String, Double> calculateIdealNegativeSolution(List<ItemCandidate> candidates) {
        Map<String, Double> idealNegative = new HashMap<>();

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();

            // Todos los criterios son de beneficio (menor es peor)
            double minValue = candidates.stream()
                    .mapToDouble(c -> c.getCriteria().getOrDefault(key, 0.0))
                    .min()
                    .orElse(0.0);

            idealNegative.put(key, minValue);
        }

        return idealNegative;
    }

    /**
     * Paso 4: Calcular distancia euclidiana a una solución ideal
     * Formula: d = sqrt(sum((v_ij - v_j*)^2))
     */
    private double calculateEuclideanDistance(ItemCandidate candidate, Map<String, Double> idealSolution) {
        double sumOfSquares = 0.0;

        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            double candidateValue = candidate.getCriteria().getOrDefault(key, 0.0);
            double idealValue = idealSolution.getOrDefault(key, 0.0);

            double difference = candidateValue - idealValue;
            sumOfSquares += difference * difference;
        }

        return Math.sqrt(sumOfSquares);
    }
}
