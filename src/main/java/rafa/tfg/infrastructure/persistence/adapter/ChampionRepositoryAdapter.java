package rafa.tfg.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafa.tfg.domain.model.Champion;
import rafa.tfg.domain.port.ChampionRepository;
import rafa.tfg.infrastructure.persistence.mapper.ChampionMapper;
import rafa.tfg.infrastructure.persistence.repository.ChampionJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto ChampionRepository
 * Conecta la capa de dominio con la infraestructura JPA
 */
@Component
@RequiredArgsConstructor
public class ChampionRepositoryAdapter implements ChampionRepository {

    private final ChampionJpaRepository jpaRepository;
    private final ChampionMapper mapper;

    @Override
    public Champion save(Champion champion) {
        var entity = mapper.toEntity(champion);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Champion> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Champion> findByChampionId(String championId) {
        return jpaRepository.findByChampionId(championId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Champion> findAll() {
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
