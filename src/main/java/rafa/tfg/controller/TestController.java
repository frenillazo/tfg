package rafa.tfg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafa.tfg.model.Champion;
import rafa.tfg.model.Item;
import rafa.tfg.repository.ChampionRepository;
import rafa.tfg.repository.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ChampionRepository championRepository;
    private final ItemRepository itemRepository;

    /**
     * Get statistics about loaded data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChampions", championRepository.count());
        stats.put("totalItems", itemRepository.count());
        stats.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all champions
     */
    @GetMapping("/champions")
    public ResponseEntity<List<Champion>> getAllChampions() {
        return ResponseEntity.ok(championRepository.findAll());
    }

    /**
     * Get champion by ID
     */
    @GetMapping("/champions/{id}")
    public ResponseEntity<Champion> getChampionById(@PathVariable String id) {
        return championRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search champions by name
     */
    @GetMapping("/champions/search")
    public ResponseEntity<List<Champion>> searchChampions(@RequestParam String name) {
        return ResponseEntity.ok(championRepository.findByNameContainingIgnoreCase(name));
    }

    /**
     * Get champions by tag
     */
    @GetMapping("/champions/tag/{tag}")
    public ResponseEntity<List<Champion>> getChampionsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(championRepository.findByTagsContaining(tag));
    }

    /**
     * Get all items
     */
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    /**
     * Get item by ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search items by name
     */
    @GetMapping("/items/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String name) {
        return ResponseEntity.ok(itemRepository.findByNameContainingIgnoreCase(name));
    }

    /**
     * Get only purchasable items
     */
    @GetMapping("/items/purchasable")
    public ResponseEntity<List<Item>> getPurchasableItems() {
        return ResponseEntity.ok(itemRepository.findByPurchasableTrue());
    }

    /**
     * Get items by tag
     */
    @GetMapping("/items/tag/{tag}")
    public ResponseEntity<List<Item>> getItemsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(itemRepository.findByTagsContaining(tag));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Test controller is running");
        return ResponseEntity.ok(response);
    }
}
