package com.micorregimiento.micorregimiento.Generics.interfaces;

public interface IFileStorageProvider {
    String upload(byte[] fileBytes, String fileName, String contentType, String folderPath) throws Exception;
    boolean delete(String fileName, String folderPath) throws Exception;
    byte[] download(String fileName, String folderPath) throws Exception;
    String getPublicUrl(String fileName, String folderPath);
    boolean exists(String fileName, String folderPath) throws Exception;
}