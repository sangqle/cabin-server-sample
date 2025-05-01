package com.cabin.demo;

import com.cabin.demo.dto.PhotoDto;
import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.services.PhotoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
    @InjectMocks
    private PhotoService photoService;

    @Test
    public void testGetPhotoByUserId() {
        int userId = 1;
    }

    @Test
    public void testGetPhotoDto() {
        int photoId = 1;
        PhotoDto photoDto = photoService.getPhotoDto(photoId);
        System.err.println(photoDto.getExif().getShootingAt());
    }

    @Test
    public void testGetSlicePhotoByUserId() {
        long userId = 1;
        int offset = 0;
        int limit = 10;
        List<PhotoDto> photos = photoService.getSlicePhotoByUserId(userId, offset, limit);
        for (PhotoDto photo : photos) {
            System.err.println(photo);
        }
    }
}