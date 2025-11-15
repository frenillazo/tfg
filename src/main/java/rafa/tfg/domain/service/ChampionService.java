package rafa.tfg.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.domain.model.Champion;
import rafa.tfg.domain.port.ChampionRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para Champion con operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChampionService {

    private final ChampionRepository championRepository;

    public Champion create(Champion champion) {
        return championRepository.save(champion);
    }

    @Transactional(readOnly = true)
    public Optional<Champion> findById(Long id) {
        return championRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Champion> findByChampionId(String championId) {
        return championRepository.findByChampionId(championId);
    }

    @Transactional(readOnly = true)
    public List<Champion> findAll() {
        return championRepository.findAll();
    }

    public Champion update(Long id, Champion champion) {
        if (!championRepository.existsById(id)) {
            throw new IllegalArgumentException("Champion with id " + id + " not found");
        }
        champion.setId(id);
        return championRepository.save(champion);
    }

    public void deleteById(Long id) {
        if (!championRepository.existsById(id)) {
            throw new IllegalArgumentException("Champion with id " + id + " not found");
        }
        championRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return championRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return championRepository.existsById(id);
    }
}
