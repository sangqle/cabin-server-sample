package com.cabin.demo.mapper;

import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.util.id.IdObfuscator;
import com.cabin.express.config.Environment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = { UserMapper.class })
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(source = "photo.id",       target = "id",   qualifiedByName = "encodePhotoId")
    @Mapping(source = "photo.rawKey", target = "rawImageUrl", qualifiedByName = "photoUrlMapping")
    @Mapping(source = "photo.webKeys", target = "urls", qualifiedByName = "photoWebUrlVersionMapping")
    @Mapping(source = "photo.title",    target = "title")
    @Mapping(source = "photo.user",     target = "user")
    @Mapping(source = "photo.photoExif",target = "exif")
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
        return String.format("%s/%s", Environment.getString("R2_BASE_URL"), objectKey);
    }

    @Named("photoWebUrlVersionMapping")
    default HashMap<String, String> photoWebUrlVersionMapping(Map<String, String> webKeys) {
        HashMap<String, String> urls = new HashMap<>();
        if (webKeys == null) {
            return urls;
        }
        for (Map.Entry<String, String> entry : webKeys.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            urls.put(key, photoUrlMapping(value));
        }
        return urls;
    }

}
