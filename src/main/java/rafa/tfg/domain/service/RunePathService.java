package rafa.tfg.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.domain.model.RunePath;
import rafa.tfg.domain.port.RunePathRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para RunePath con operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RunePathService {

    private final RunePathRepository runePathRepository;

    public RunePath create(RunePath runePath) {
        return runePathRepository.save(runePath);
    }

    @Transactional(readOnly = true)
    public Optional<RunePath> findById(Long id) {
        return runePathRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RunePath> findByPathId(Integer pathId) {
        return runePathRepository.findByPathId(pathId);
    }

    @Transactional(readOnly = true)
    public List<RunePath> findAll() {
        return runePathRepository.findAll();
    }

    public RunePath update(Long id, RunePath runePath) {
        if (!runePathRepository.existsById(id)) {
            throw new IllegalArgumentException("RunePath with id " + id + " not found");
        }
        runePath.setId(id);
        return runePathRepository.save(runePath);
    }

    public void deleteById(Long id) {
        if (!runePathRepository.existsById(id)) {
            throw new IllegalArgumentException("RunePath with id " + id + " not found");
        }
        runePathRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return runePathRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return runePathRepository.existsById(id);
    }
}
