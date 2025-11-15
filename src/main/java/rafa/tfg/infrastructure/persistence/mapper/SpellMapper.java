package rafa.tfg.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.infrastructure.persistence.entity.SpellEntity;

import java.util.List;

/**
 * Mapper de MapStruct para convertir entre Spell (dominio) y SpellEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface SpellMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "levelTipLabels", source = "levelTipLabels", qualifiedByName = "listStringToJson")
    @Mapping(target = "levelTipEffects", source = "levelTipEffects", qualifiedByName = "listStringToJson")
    @Mapping(target = "cooldown", source = "cooldown", qualifiedByName = "listDoubleToJson")
    @Mapping(target = "cost", source = "cost", qualifiedByName = "listIntegerToJson")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "listListDoubleToJson")
    @Mapping(target = "effectBurn", source = "effectBurn", qualifiedByName = "listStringToJson")
    @Mapping(target = "range", source = "range", qualifiedByName = "listLongToJson")
    SpellEntity toEntity(Spell spell);

    @Mapping(target = "levelTipLabels", source = "levelTipLabels", qualifiedByName = "jsonToListString")
    @Mapping(target = "levelTipEffects", source = "levelTipEffects", qualifiedByName = "jsonToListString")
    @Mapping(target = "cooldown", source = "cooldown", qualifiedByName = "jsonToListDouble")
    @Mapping(target = "cost", source = "cost", qualifiedByName = "jsonToListInteger")
    @Mapping(target = "effect", source = "effect", qualifiedByName = "jsonToListListDouble")
    @Mapping(target = "effectBurn", source = "effectBurn", qualifiedByName = "jsonToListString")
    @Mapping(target = "range", source = "range", qualifiedByName = "jsonToListLong")
    Spell toDomain(SpellEntity entity);

    List<Spell> toDomainList(List<SpellEntity> entities);

    // Conversiones List<String> <-> JSON
    @Named("listStringToJson")
    default String listStringToJson(List<String> list) {
        if (list == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("jsonToListString")
    default List<String> jsonToListString(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // Conversiones List<Double> <-> JSON
    @Named("listIntegerToJson")
    default String listIntegerToJson(List<Integer> list) {
        if (list == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("jsonToListDouble")
    default List<Double> jsonToListDouble(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<Double>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("listLongToJson")
    default String listLongToJson(List<Long> list) {
        if (list == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // Conversiones List<Integer> <-> JSON
    @Named("listDoubleToJson")
    default String listDoubleToJson(List<Double> list) {
        if (list == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("jsonToListInteger")
    default List<Integer> jsonToListInteger(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("jsonToListLong")
    default List<Long> jsonToListLong(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // Conversiones List<List<Double>> <-> JSON
    @Named("listListDoubleToJson")
    default String listListDoubleToJson(List<List<Double>> list) {
        if (list == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("jsonToListListDouble")
    default List<List<Double>> jsonToListListDouble(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<List<Double>>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
