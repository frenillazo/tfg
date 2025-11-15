package rafa.tfg.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.infrastructure.persistence.entity.SpellEntity;

import java.util.List;

/**
 * Mapper de MapStruct para convertir entre Spell (dominio) y SpellEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface SpellMapper {

    SpellEntity toEntity(Spell spell);

    Spell toDomain(SpellEntity entity);

    List<Spell> toDomainList(List<SpellEntity> entities);
}
