package rafa.tfg.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafa.tfg.application.dto.recommendation.GameStateRequestDTO;
import rafa.tfg.application.dto.recommendation.ItemRecommendationResponseDTO;
import rafa.tfg.domain.service.recommendation.ItemRecommendationService;

/**
 * Controlador REST para el sistema de recomendaciones de items
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final ItemRecommendationService itemRecommendationService;

    /**
     * Endpoint para obtener recomendaciones de items basadas en el estado del juego
     *
     * @param gameState Estado actual del juego (JSON de la API de League of Legends)
     * @return Top 5 items recomendados con scores y explicaciones
     */
    @PostMapping("/items")
    public ResponseEntity<ItemRecommendationResponseDTO> recommendItems(
            @RequestBody GameStateRequestDTO gameState) {

        log.info("Received item recommendation request for player: {}",
                gameState.getActivePlayer().getSummonerName());

        try {
            ItemRecommendationResponseDTO response = itemRecommendationService.recommendItems(gameState);

            log.info("Recommendation request processed successfully. Returned {} recommendations",
                    response.getRecommendations().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing recommendation request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de health check para el servicio de recomendaciones
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Item Recommendation Service is running");
    }
}
