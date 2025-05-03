package com.cabin.demo.util.photo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    static {
        ImageIO.scanForPlugins();
    }

    /**
     * Chuyển bất kỳ ảnh RAW/​TIFF/​PNG… nào sang JPEG,
     * giữ nguyên chiều rộng và cao gốc.
     *
     * @param inputBytes mảng byte ảnh gốc
     * @return mảng byte JPEG cùng kích thước
     * @throws IOException nếu đọc/ghi thất bại
     */
    public static byte[] toJpeg(byte[] inputBytes) throws IOException {
        // 1. Đọc ảnh gốc thành BufferedImage (giờ hỗ trợ RAW & TIFF JPEG-compressed)
        BufferedImage original = ImageIO.read(
                new ByteArrayInputStream(inputBytes)
        );
        if (original == null) {
            throw new IOException("Không thể đọc ảnh: định dạng không hỗ trợ");
        }

        // 2. Nếu ảnh có alpha, vẽ lên background trắng để loại bỏ (JPEG không hỗ trợ alpha)
        BufferedImage rgb = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        rgb.getGraphics().drawImage(original, 0, 0, Color.WHITE, null);

        // 3. Ghi ra JPEG, giữ nguyên kích thước
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(rgb, "jpg", os);
            return os.toByteArray();
        }
    }

    /**
     * Chuyển ảnh sang PNG, giữ nguyên alpha và kích thước gốc.
     */
    public static byte[] toPng(byte[] inputBytes) throws IOException {
        BufferedImage original = ImageIO.read(
                new ByteArrayInputStream(inputBytes)
        );
        if (original == null) {
            throw new IOException("Không thể đọc ảnh: định dạng không hỗ trợ");
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(original, "png", os);
            return os.toByteArray();
        }
    }
}
