package com.pst.support.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	/**
	 * Saves the uploaded file to the specified file path.
	 *
	 * @param file The uploaded file to be stored
	 * @throws IOException If there is an issue writing the file to the disk
	 */
	public String addFile(MultipartFile file) throws IOException {
	    if (file.isEmpty()) {
	        throw new IOException("Le fichier est vide");
	    }

	    // Définir le chemin relatif au répertoire du projet (par exemple, dans le dossier 'public/uploads')
	    String projectRoot = System.getProperty("user.dir"); // Récupère le chemin racine du projet
	    String filePathComplete = projectRoot + "/src/main/resources/static/uploads/"; // Chemin de destination dans le projet

	    // Corriger le nom du fichier pour éviter les espaces
	    String correctedName = file.getOriginalFilename().replaceAll(" ", "_");

	    Path dirPath = Paths.get(filePathComplete);

	    // Crée les répertoires si non présents
	    if (!Files.exists(dirPath)) {
	        Files.createDirectories(dirPath);
	    }

	    // Créer l'objet fichier avec le chemin correct
	    File uploadedFile = new File(dirPath.toFile(), correctedName);

	    // Transférer le fichier au chemin de destination
	    file.transferTo(uploadedFile);

	    // Retourner le chemin absolu du fichier téléchargé
	    return uploadedFile.getAbsolutePath();
	}
	
	/**
	 * Loads the file as a resource from the file system.
	 * 
	 * @param path the complete path of the file to load
	 * @return the resource of the file
	 * @throws IOException if the file is not found or cannot be read
	 */
	public Resource loadFileAsResource(String path) throws IOException {
		try {
			// Convert the provided path to a Path object
			Path filePath = Paths.get(path).normalize();

			// Ensure the file exists and is readable
			if (Files.notExists(filePath) || !Files.isReadable(filePath)) {
				throw new IOException("File not found or not readable: " + path);
			}

			// Create a FileSystemResource with the normalized path
			Resource resource = new FileSystemResource(filePath);

			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				throw new IOException("File not found: " + path);
			}
		} catch (MalformedURLException ex) {
			throw new IOException("Invalid file path: " + path, ex);
		}
	}

	/**
     * Determines the MediaType of a Resource based on its filename extension.
     *
     * @param resource The resource to analyze
     * @return The MediaType based on the file extension
     */
    public MediaType getMediaTypeFromResource(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            return MediaType.ALL; // Return ALL if filename is not available
        }

        String fileExtension = getFileExtension(filename).toLowerCase();

        // Map file extensions to MediaTypes
        switch (fileExtension) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "html":
                return MediaType.TEXT_HTML;
            case "xml":
                return MediaType.APPLICATION_XML;
            // Add more file types as needed
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // Fallback for unknown types
        }
    }

    /**
     * Extracts the file extension from a filename.
     *
     * @param filename The filename to extract the extension from
     * @return The file extension
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }
}
