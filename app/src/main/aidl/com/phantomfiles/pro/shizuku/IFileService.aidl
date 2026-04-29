package com.phantomfiles.pro.shizuku;

interface IFileService {
    List<String> listFiles(String path);
    boolean copyFile(String source, String dest);
    boolean moveFile(String source, String dest);
    boolean deleteFile(String path);
    boolean createDirectory(String path);
    long getFileSize(String path);
    String readFileContent(String path, int maxBytes);
    byte[] readFileBytes(String path, int offset, int length);
}
