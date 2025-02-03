package org.teamchallenge.bookshop.util;

import org.imgscalr.Scalr;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        float aspectRatio = (float) originalWidth / originalHeight;

        if (originalWidth > originalHeight) {
            targetHeight = Math.round(targetWidth / aspectRatio);
        } else {
            targetWidth = Math.round(targetHeight * aspectRatio);
        }

        return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight);
    }
    public static BufferedImage base64ToBufferedImage(String base64String) {
        // Перевірка на null або порожній рядок
        if (base64String == null || base64String.trim().isEmpty()) {
            throw new RuntimeException("Base64 string is null or empty");
        }

        // Видалення префіксу, якщо присутній
        if (base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }

        try {
            // Декодування Base64 у байти
            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            // Конвертація у BufferedImage
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                BufferedImage image = ImageIO.read(bais);
                if (image == null) {
                    throw new RuntimeException("Failed to decode Base64 to BufferedImage: Invalid image data");
                }
                return image;
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 format", e);
        } catch (IOException e) {
            throw new RuntimeException("Error decoding Base64 to BufferedImage", e);
        }
    }

    public static MultipartFile base64ToMultipartFile(String base64) {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Base64 string is null or empty");
        }

        try {
            // Відокремлення метаданих Base64 (якщо є)
            String[] parts = base64.split(",");
            byte[] fileBytes = Base64.getDecoder().decode(parts.length > 1 ? parts[1] : parts[0]);

            // Створення MultipartFile
            return new MockMultipartFile(
                    "file",               // Назва файлу
                    "image.png",          // Початкове ім'я файлу
                    "image/png",          // MIME-тип
                    new ByteArrayInputStream(fileBytes)
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 input", e);
        } catch (IOException e) {
            throw new RuntimeException("Error converting Base64 to MultipartFile", e);
        }
    }

    public static String bufferedImageToBase64(BufferedImage image) {
        try {
            byte[] imageBytes = bufferedImageToBytes(image);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] bufferedImageToBytes(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}