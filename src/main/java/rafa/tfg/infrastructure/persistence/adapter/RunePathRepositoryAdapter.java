package rafa.tfg.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafa.tfg.domain.model.RunePath;
import rafa.tfg.domain.port.RunePathRepository;
import rafa.tfg.infrastructure.persistence.mapper.RunePathMapper;
import rafa.tfg.infrastructure.persistence.repository.RunePathJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto RunePathRepository
 * Conecta la capa de dominio con la infraestructura JPA
 */
@Component
@RequiredArgsConstructor
public class RunePathRepositoryAdapter implements RunePathRepository {

    private final RunePathJpaRepository jpaRepository;
    private final RunePathMapper mapper;

    @Override
    public RunePath save(RunePath runePath) {
        var entity = mapper.toEntity(runePath);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RunePath> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RunePath> findByPathId(Integer pathId) {
        return jpaRepository.findByPathId(pathId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RunePath> findAll() {
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
}
