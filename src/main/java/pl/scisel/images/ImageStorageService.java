package pl.scisel.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageStorageService {

    private final Path rootLocation;
    private final String defaultImage;

    @Autowired
    public ImageStorageService(@Value("${image.storage.location}") String storageLocation,
                               @Value("${item.default.image}") String defaultImage) {
        this.rootLocation = Paths.get(storageLocation);
        this.defaultImage = defaultImage;
    }

    public String store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file " + filename);
        }
        Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                .normalize().toAbsolutePath();
        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new RuntimeException("Cannot store file outside current directory.");
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return file.getOriginalFilename();
    }

    public String getImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            // Zwróć URL do obrazu domyślnego
            return MvcUriComponentsBuilder
                    .fromMethodName(UploadController.class, "serveFile", this.defaultImage)
                    .build().toUri().toString();
        } else {
            // Zwróć URL do obrazu przedmiotu
            return MvcUriComponentsBuilder
                    .fromMethodName(UploadController.class, "serveFile", filename)
                    .build().toUri().toString();
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
}
