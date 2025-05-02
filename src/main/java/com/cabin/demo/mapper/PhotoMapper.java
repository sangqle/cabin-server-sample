package com.cabin.demo.mapper;

import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.util.id.IdObfuscator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class})
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(source = "photo.user", target = "user")
    @Mapping(source = "photo.photoExif", target = "exif")
    @Mapping(source = "photo.id", target = "id", qualifiedByName = "encodePhotoId")
    PhotoDto toDto(Photo photo);

    @Named("encodePhotoId")
    default String encodeId(Long id) {
        try {
            return IdObfuscator.encodePhotoId(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode ID", e);
        }
    }
}
