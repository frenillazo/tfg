package rafa.tfg.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafa.tfg.domain.model.Rune;
import rafa.tfg.domain.port.RuneRepository;
import rafa.tfg.infrastructure.persistence.mapper.RuneMapper;
import rafa.tfg.infrastructure.persistence.repository.RuneJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto RuneRepository
 * Conecta la capa de dominio con la infraestructura JPA
 */
@Component
@RequiredArgsConstructor
public class RuneRepositoryAdapter implements RuneRepository {

    private final RuneJpaRepository jpaRepository;
    private final RuneMapper mapper;

    @Override
    public Rune save(Rune rune) {
        var entity = mapper.toEntity(rune);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Rune> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Rune> findByRuneId(Integer runeId) {
        return jpaRepository.findByRuneId(runeId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Rune> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Rune> findByRunePathId(Long runePathId) {
        return mapper.toDomainList(jpaRepository.findByRunePathId(runePathId));
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
}
