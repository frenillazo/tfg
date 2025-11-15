package rafa.tfg.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.domain.port.SpellRepository;
import rafa.tfg.infrastructure.persistence.mapper.SpellMapper;
import rafa.tfg.infrastructure.persistence.repository.SpellJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto SpellRepository
 * Conecta la capa de dominio con la infraestructura JPA
 */
@Component
@RequiredArgsConstructor
public class SpellRepositoryAdapter implements SpellRepository {

    private final SpellJpaRepository jpaRepository;
    private final SpellMapper mapper;

    @Override
    public Spell save(Spell spell) {
        var entity = mapper.toEntity(spell);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Spell> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Spell> findBySpellId(String spellId) {
        return jpaRepository.findBySpellId(spellId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Spell> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Spell> findByChampionId(String championId) {
        return jpaRepository.findByChampionId(championId);
    }
}
