package rafa.tfg.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.domain.model.Rune;
import rafa.tfg.domain.port.RuneRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para Rune con operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RuneService {

    private final RuneRepository runeRepository;

    public Rune create(Rune rune) {
        return runeRepository.save(rune);
    }

    @Transactional(readOnly = true)
    public Optional<Rune> findById(Long id) {
        return runeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Rune> findByRuneId(Integer runeId) {
        return runeRepository.findByRuneId(runeId);
    }

    @Transactional(readOnly = true)
    public List<Rune> findAll() {
        return runeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rune> findByRunePathId(Long runePathId) {
        return runeRepository.findByRunePathId(runePathId);
    }

    public Rune update(Long id, Rune rune) {
        if (!runeRepository.existsById(id)) {
            throw new IllegalArgumentException("Rune with id " + id + " not found");
        }
        rune.setId(id);
        return runeRepository.save(rune);
    }

    public void deleteById(Long id) {
        if (!runeRepository.existsById(id)) {
            throw new IllegalArgumentException("Rune with id " + id + " not found");
        }
        runeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return runeRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return runeRepository.existsById(id);
    }
}
