package com.cabin.demo.util.photo;

import org.apache.commons.imaging.ImageReadException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    // ... your existing resize(...) and presets ...

    /**
     * Converts any supported image format (RAW, PNG, TIFF, etc.) to JPEG,
     * preserving the original width and height.
     *
     * @param inputBytes the original image data
     * @return JPEG-encoded bytes of the same dimensions
     * @throws IOException
     * @throws ImageReadException
     */
    public static byte[] toJpeg(byte[] inputBytes) throws IOException {
        // Decode using ImageIO with TwelveMonkeys plugin available
        BufferedImage original = ImageIO.read(
                new ByteArrayInputStream(inputBytes)
        );

        // Handle images with alpha by drawing on white background
        BufferedImage rgb = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        rgb.getGraphics().drawImage(original, 0, 0, java.awt.Color.WHITE, null);

        // Encode as JPEG
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(rgb, "jpg", os);
            return os.toByteArray();
        }
    }
}
