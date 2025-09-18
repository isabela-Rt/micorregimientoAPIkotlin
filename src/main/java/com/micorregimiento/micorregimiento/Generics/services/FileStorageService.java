package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IFileStorageService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IFileStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageService implements IFileStorageService {

    @Value("${file.storage.provider:minio}") // default: minio, options: minio, drive
    private String storageProvider;

    @Autowired(required = false)
    private IFileStorageProvider minioProvider;

    @Autowired(required = false)
    private IFileStorageProvider driveProvider;

    @Override
    public String uploadFile(MultipartFile file, String folderPath) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        byte[] fileBytes = file.getBytes();
        String contentType = file.getContentType();

        IFileStorageProvider provider = getProvider();
        String uploadedPath = provider.upload(fileBytes, fileName, contentType, folderPath);

        return provider.getPublicUrl(fileName, folderPath);
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String contentType, String folderPath) throws Exception {
        byte[] fileBytes = inputStream.readAllBytes();
        String uniqueFileName = generateUniqueFileName(fileName);

        IFileStorageProvider provider = getProvider();
        provider.upload(fileBytes, uniqueFileName, contentType, folderPath);

        return provider.getPublicUrl(uniqueFileName, folderPath);
    }

    @Override
    public boolean deleteFile(String fileUrl) throws Exception {
        String[] pathParts = extractFilePathFromUrl(fileUrl);
        String folderPath = pathParts[0];
        String fileName = pathParts[1];

        IFileStorageProvider provider = getProvider();
        return provider.delete(fileName, folderPath);
    }

    @Override
    public InputStream downloadFile(String fileUrl) throws Exception {
        String[] pathParts = extractFilePathFromUrl(fileUrl);
        String folderPath = pathParts[0];
        String fileName = pathParts[1];

        IFileStorageProvider provider = getProvider();
        byte[] fileBytes = provider.download(fileName, folderPath);

        return new ByteArrayInputStream(fileBytes);
    }

    @Override
    public String generatePublicUrl(String fileName, String folderPath) {
        IFileStorageProvider provider = getProvider();
        return provider.getPublicUrl(fileName, folderPath);
    }

    @Override
    public boolean fileExists(String fileUrl) throws Exception {
        String[] pathParts = extractFilePathFromUrl(fileUrl);
        String folderPath = pathParts[0];
        String fileName = pathParts[1];

        IFileStorageProvider provider = getProvider();
        return provider.exists(fileName, folderPath);
    }

    private IFileStorageProvider getProvider() {
        return switch (storageProvider.toLowerCase()) {
            case "drive" -> {
                if (driveProvider == null) {
                    throw new RuntimeException("Google Drive provider no está configurado");
                }
                yield driveProvider;
            }
            case "minio" -> {
                if (minioProvider == null) {
                    throw new RuntimeException("MinIO provider no está configurado");
                }
                yield minioProvider;
            }
            default -> throw new RuntimeException("Provider de almacenamiento no soportado: " + storageProvider);
        };
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf(".") > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String[] extractFilePathFromUrl(String fileUrl) {
        // Implementar lógica para extraer folder y fileName de la URL
        // Esto dependerá del formato de URL que uses
        // Por ejemplo: https://domain.com/folder/subfolder/filename.jpg

        // Implementación básica - ajustar según tu estructura de URLs
        String[] parts = fileUrl.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("URL de archivo inválida: " + fileUrl);
        }

        String fileName = parts[parts.length - 1];
        String folderPath = String.join("/", java.util.Arrays.copyOfRange(parts, 0, parts.length - 1));

        return new String[]{folderPath, fileName};
    }
}
