package com.pst.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	
	@Value("${FILE_PATH}")
	private static String filePath;
	
	
	/**
     * Saves the uploaded file to the specified file path.
     *
     * @param file The uploaded file to be stored
     * @throws IOException If there is an issue writing the file to the disk
     */
    public static String addFile(MultipartFile file) throws IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Create the directory if it does not exist
        Path dirPath = Paths.get(filePath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Generate a file path where the file will be stored
        Path fileStoragePath = dirPath.resolve(file.getOriginalFilename());

        // Store the file
        file.transferTo(fileStoragePath.toFile());

       	return fileStoragePath.toString();
    }
}
