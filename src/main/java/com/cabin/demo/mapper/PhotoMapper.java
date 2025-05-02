package com.cabin.demo.mapper;

import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.express.config.Environment;
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
    @Mapping(source = "photo.objectKey", target = "photoUrl", qualifiedByName = "photoUrlMapping")
    PhotoDto toDto(Photo photo);

    @Named("encodePhotoId")
    default String encodeId(Long id) {
        try {
            return IdObfuscator.encodePhotoId(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode ID", e);
        }
    }

    @Named("photoUrlMapping")
    default String photoUrlMapping(String objectKey) {
        if (objectKey == null) {
            return null;
        }
        return String.format("%s/%s", Environment.getString("S3_BASE_URL"), objectKey);
    }
}
