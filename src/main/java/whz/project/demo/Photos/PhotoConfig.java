package whz.project.demo.Photos;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import whz.project.demo.exceptions.CannotUploadImageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Data
@Component
public class PhotoConfig {

    private final String uploadDir = "src/main/resources/static/img";
    private final Path path = Paths.get(uploadDir);

    public String savePhoto(MultipartFile file) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String extension = getFileExtension(file.getOriginalFilename());
            String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

            Path fullPath = path.resolve(uniqueFilename);
            Files.write(fullPath, file.getBytes());

            return uniqueFilename;

        } catch (IOException e) {
            throw new CannotUploadImageException("Bild konnte nicht hochgeladen werden.");
        }
    }

    private String getFileExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "jpg"; // fallback
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1);
    }
}
