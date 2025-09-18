package com.micorregimiento.micorregimiento.Generics.interfaces;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface IFileStorageService {
    String uploadFile(MultipartFile file, String folderPath) throws Exception;
    String uploadFile(InputStream inputStream, String fileName, String contentType, String folderPath) throws Exception;
    boolean deleteFile(String fileUrl) throws Exception;
    InputStream downloadFile(String fileUrl) throws Exception;
    String generatePublicUrl(String fileName, String folderPath);
    boolean fileExists(String fileUrl) throws Exception;
}