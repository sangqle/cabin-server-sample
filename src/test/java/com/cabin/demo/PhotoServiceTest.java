package com.cabin.demo;

import com.cabin.demo.entity.photo.Photo;
import com.cabin.demo.services.PhotoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {
    @InjectMocks
    private PhotoService photoService;

    @Test
    public void testGetPhotoByUserId() {
        int userId = 1;
        Photo photoById = photoService.getPhotoById(1);
        System.err.println(photoById.getUser().getName());
        System.err.println(photoById.getPhotoExif().getCameraModel());

    }
}