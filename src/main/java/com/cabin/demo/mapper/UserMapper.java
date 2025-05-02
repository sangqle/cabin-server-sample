package com.cabin.demo.mapper;

import com.cabin.demo.dto.UserDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.util.id.IdObfuscator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "user.id", target = "id", qualifiedByName = "encodeUserId")
    UserDto toDto(User user);

    @Named("encodeUserId")
    default String encodeId(Long id) {
        try {
            System.err.println("encodeId: " + id);
            return IdObfuscator.encodeId(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode ID", e);
        }
    }
}
