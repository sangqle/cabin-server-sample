package com.cabin.demo.mapper;

import com.cabin.demo.dto.UserDto;
import com.cabin.demo.entity.auth.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto toDto(User user);
}
