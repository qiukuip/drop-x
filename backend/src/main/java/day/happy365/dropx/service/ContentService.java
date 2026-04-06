package day.happy365.dropx.service;

import day.happy365.dropx.mapper.ContentMapper;
import day.happy365.dropx.model.Content;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.StatObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ContentService {

    private final ContentMapper contentMapper;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public ContentService(ContentMapper contentMapper, MinioClient minioClient) {
        this.contentMapper = contentMapper;
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String objectName = file.getOriginalFilename();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return objectName;
    }

    public String saveTextContent(String text) throws Exception {
        String objectName = "text_" + System.currentTimeMillis() + ".txt";
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, bytes.length, -1)
                            .contentType("text/plain")
                            .build()
            );
        }
        return objectName;
    }

    public InputStream getFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public String getFileName(String objectName) {
        if (objectName.contains("_")) {
            String[] parts = objectName.split("_");
            if (parts.length >= 3) {
                String fileName = objectName.substring(parts[0].length() + parts[1].length() + 2);
                if (fileName.contains(".")) {
                    return fileName;
                }
            }
        }
        return objectName;
    }

    public String getContentType(String objectName) {
        if (objectName.endsWith(".txt")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }

    public String generateExtractCode() {
        Random random = new Random();
        String code;
        do {
            code = String.format("%05d", random.nextInt(100000));
        } while (contentMapper.countByExtractCode(code) > 0);
        return code;
    }

    public void saveContent(String extractCode, String contentType, String objectName) {
        Content content = new Content();
        content.setExtractCode(extractCode);
        content.setContentType(contentType);
        content.setStatus("Y");
        content.setExpireAt(LocalDateTime.now().plusHours(24));
        content.setObjectName(objectName);
        contentMapper.insert(content);
    }

    public Content getContent(String extractCode, String contentType) {
        return contentMapper.findByExtractCode(extractCode, contentType);
    }

    public long getObjectSize(String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        ).size();
    }
}
