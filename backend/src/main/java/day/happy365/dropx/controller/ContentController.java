package day.happy365.dropx.controller;

import day.happy365.dropx.model.ApiResponse;
import day.happy365.dropx.model.Content;
import day.happy365.dropx.service.ContentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/files")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.fail("请选择文件");
            }
            if (file.getSize() > 5L * 1024 * 1024 * 1024) {
                return ApiResponse.fail("文件大小不能超过5GB");
            }

            String extractCode = contentService.generateExtractCode();
            String objectName = contentService.uploadFile(file);
            contentService.saveContent(extractCode, "file", objectName);

            return ApiResponse.success("文件上传成功", extractCode);
        } catch (Exception e) {
            return ApiResponse.fail("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/text")
    public ApiResponse<String> saveText(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.fail("请输入文本内容");
            }

            String extractCode = contentService.generateExtractCode();
            String objectName = contentService.saveTextContent(content);
            contentService.saveContent(extractCode, "text", objectName);

            return ApiResponse.success("文本保存成功", extractCode);
        } catch (Exception e) {
            return ApiResponse.fail("文本保存失败: " + e.getMessage());
        }
    }

    @GetMapping("/files/{extractCode}")
    public void downloadFile(@PathVariable String extractCode, HttpServletResponse response) {
        try {
            Content content = contentService.getContent(extractCode, "file");
            if (content == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在或已过期");
                return;
            }

            String objectName = content.getObjectName();
            InputStream inputStream = contentService.getFile(objectName);

            response.setContentType(contentService.getContentType(objectName));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + contentService.getFileName(objectName) + "\"");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @GetMapping("/text/{extractCode}")
    public ApiResponse<String> getText(@PathVariable String extractCode) {
        try {
            Content content = contentService.getContent(extractCode, "text");
            if (content == null) {
                return ApiResponse.fail("文本内容不存在或已过期");
            }

            String objectName = content.getObjectName();
            InputStream inputStream = contentService.getFile(objectName);
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            inputStream.close();

            return ApiResponse.success(text);
        } catch (Exception e) {
            return ApiResponse.fail("获取文本失败: " + e.getMessage());
        }
    }
}
