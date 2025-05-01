package com.cabin.demo.mapper;

import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.dto.PhotoExifDto;
import com.cabin.demo.dto.UserDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.entity.photo.PhotoExif;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(source = "photo.user", target = "user")
    @Mapping(source = "photo.photoExif", target = "exif")
    PhotoDto toDto(Photo photo);

    UserDto toDto(User user);
    PhotoExifDto toDto(PhotoExif exif);
}
