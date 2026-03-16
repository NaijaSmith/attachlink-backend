package com.attachlink.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Interface defining storage operations.
 */
public interface StorageService {
    String store(MultipartFile file, String folder);
    void delete(String filePath);
}

/**
 * CONCRETE IMPLEMENTATION: This resolves the UnsatisfiedDependencyException.
 * The @Service annotation tells Spring to create a bean for this class.
 */
@Service
class FileSystemStorageService implements StorageService {

    private final Path rootLocation = Paths.get("upload-dir");

    public FileSystemStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            Path destinationFolder = this.rootLocation.resolve(folder);
            Files.createDirectories(destinationFolder);

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationFile = destinationFolder.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            file.transferTo(destinationFile);
            return destinationFile.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }
}