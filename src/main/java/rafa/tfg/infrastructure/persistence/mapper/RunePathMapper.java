package rafa.tfg.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rafa.tfg.domain.model.RunePath;
import rafa.tfg.infrastructure.persistence.entity.RunePathEntity;

import java.util.List;

/**
 * Mapper de MapStruct para convertir entre RunePath (dominio) y RunePathEntity (JPA)
 */
@Mapper(componentModel = "spring", uses = {RuneMapper.class})
public interface RunePathMapper {

    @Mapping(target = "runes", ignore = true)  // Managed separately to avoid circular dependency
    RunePathEntity toEntity(RunePath runePath);

    @Mapping(target = "runes", ignore = true)  // Managed separately
    RunePath toDomain(RunePathEntity entity);

    List<RunePath> toDomainList(List<RunePathEntity> entities);
}
