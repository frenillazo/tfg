package rafa.tfg.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafa.tfg.domain.model.*;
import rafa.tfg.domain.service.*;

import java.util.*;

/**
 * Controller REST para consultar los datos cargados
 */
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final ChampionService championService;
    private final ItemService itemService;
    private final RuneService runeService;
    private final RunePathService runePathService;
    private final SpellService spellService;

    /**
     * Obtener estadísticas generales de datos cargados
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("champions", championService.count());
        stats.put("items", itemService.count());
        stats.put("runes", runeService.count());
        stats.put("runePaths", runePathService.count());
        stats.put("spells", spellService.count());
        return ResponseEntity.ok(stats);
    }

    // ========== Champions ==========

    /**
     * Obtener todos los champions
     */
    @GetMapping("/champions")
    public ResponseEntity<List<Champion>> getAllChampions() {
        return ResponseEntity.ok(championService.findAll());
    }

    /**
     * Obtener un champion por ID
     */
    @GetMapping("/champions/{id}")
    public ResponseEntity<Champion> getChampionById(@PathVariable Long id) {
        return championService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener un champion por championId (ej: "Aatrox")
     */
    @GetMapping("/champions/by-name/{championId}")
    public ResponseEntity<Champion> getChampionByChampionId(@PathVariable String championId) {
        return championService.findByChampionId(championId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Items ==========

    /**
     * Obtener todos los items
     */
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    /**
     * Obtener un item por ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener un item por itemId (ej: "1001")
     */
    @GetMapping("/items/by-code/{itemId}")
    public ResponseEntity<Item> getItemByItemId(@PathVariable String itemId) {
        return itemService.findByItemId(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Runes ==========

    /**
     * Obtener todas las runas
     */
    @GetMapping("/runes")
    public ResponseEntity<List<Rune>> getAllRunes() {
        return ResponseEntity.ok(runeService.findAll());
    }

    /**
     * Obtener una runa por ID
     */
    @GetMapping("/runes/{id}")
    public ResponseEntity<Rune> getRuneById(@PathVariable Long id) {
        return runeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener runas por path ID
     */
    @GetMapping("/runes/by-path/{runePathId}")
    public ResponseEntity<List<Rune>> getRunesByPathId(@PathVariable Long runePathId) {
        return ResponseEntity.ok(runeService.findByRunePathId(runePathId));
    }

    // ========== Rune Paths ==========

    /**
     * Obtener todos los rune paths
     */
    @GetMapping("/rune-paths")
    public ResponseEntity<List<RunePath>> getAllRunePaths() {
        return ResponseEntity.ok(runePathService.findAll());
    }

    /**
     * Obtener un rune path por ID
     */
    @GetMapping("/rune-paths/{id}")
    public ResponseEntity<RunePath> getRunePathById(@PathVariable Long id) {
        return runePathService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== Spells ==========

    /**
     * Obtener todos los spells
     */
    @GetMapping("/spells")
    public ResponseEntity<List<Spell>> getAllSpells() {
        return ResponseEntity.ok(spellService.findAll());
    }

    /**
     * Obtener un spell por ID
     */
    @GetMapping("/spells/{id}")
    public ResponseEntity<Spell> getSpellById(@PathVariable Long id) {
        return spellService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
