package rafa.tfg.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.domain.port.SpellRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para Spell con operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SpellService {

    private final SpellRepository spellRepository;

    public Spell create(Spell spell) {
        return spellRepository.save(spell);
    }

    @Transactional(readOnly = true)
    public Optional<Spell> findById(Long id) {
        return spellRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Spell> findBySpellId(Long spellId) {
        return spellRepository.findBySpellId(spellId);
    }

    @Transactional(readOnly = true)
    public List<Spell> findAll() {
        return spellRepository.findAll();
    }

    public Spell update(Long id, Spell spell) {
        if (!spellRepository.existsById(id)) {
            throw new IllegalArgumentException("Spell with id " + id + " not found");
        }
        spell.setId(id);
        return spellRepository.save(spell);
    }

    public void deleteById(Long id) {
        if (!spellRepository.existsById(id)) {
            throw new IllegalArgumentException("Spell with id " + id + " not found");
        }
        spellRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return spellRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return spellRepository.existsById(id);
    }
}
