package day.happy365.dropx.service;

import day.happy365.dropx.mapper.ContentMapper;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CleanupService {
    @Autowired
    private ContentMapper contentMapper;

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredContent() {
        try {
            contentMapper.updateExpiredStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
