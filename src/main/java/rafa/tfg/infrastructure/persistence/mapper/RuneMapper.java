package rafa.tfg.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rafa.tfg.domain.model.Rune;
import rafa.tfg.infrastructure.persistence.entity.RuneEntity;

import java.util.List;

/**
 * Mapper de MapStruct para convertir entre Rune (dominio) y RuneEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface RuneMapper {

    @Mapping(target = "runePath", ignore = true)  // Managed by RunePathMapper
    @Mapping(target = "runePathId", source = "runePath.id")
    RuneEntity toEntity(Rune rune);

    @Mapping(target = "runePathId", source = "runePath.id")
    Rune toDomain(RuneEntity entity);

    List<Rune> toDomainList(List<RuneEntity> entities);
}
