package whz.project.demo.Photos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import whz.project.demo.exceptions.CannotUploadImageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class PhotoConfig {
    private String uploadDir = "static/img";
    private Path path = Paths.get(uploadDir);

    public String savePhoto(MultipartFile file){
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ignored) {

            }
        }

        try {
            byte[] bytes = file.getBytes();
            Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
            Files.write(filePath, bytes);

            return file.getOriginalFilename();

        } catch (IOException e) {
            throw new CannotUploadImageException("Can not upload a photo!");
        }
    }


}
