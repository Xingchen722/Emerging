package com.networkinsights.ai.controller;

import com.networkinsights.ai.service.DocumentProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 处理 PDF 和其他文档的上传和解析
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private DocumentProcessingService documentProcessingService;

    /**
     * 上传 PDF 文件
     */
    @PostMapping("/pdf")
    public ResponseEntity<Map<String, Object>> uploadPDF(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                response.put("success", false);
                response.put("message", "只支持 PDF 文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 处理 PDF 文件
            String extractedText = documentProcessingService.extractTextFromPDF(file);
            int dataCount = documentProcessingService.parseAndStorePSAData(extractedText);

            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("extractedTextLength", extractedText.length());
            response.put("parsedDataCount", dataCount);

            logger.info("PDF文件上传成功: {}, 提取文本长度: {}, 解析数据条数: {}", 
                       file.getOriginalFilename(), extractedText.length(), dataCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("PDF文件上传失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 上传文本文件
     */
    @PostMapping("/text")
    public ResponseEntity<Map<String, Object>> uploadText(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 处理文本文件
            String content = new String(file.getBytes(), "UTF-8");
            int dataCount = documentProcessingService.parseAndStorePSAData(content);

            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentLength", content.length());
            response.put("parsedDataCount", dataCount);

            logger.info("文本文件上传成功: {}, 内容长度: {}, 解析数据条数: {}", 
                       file.getOriginalFilename(), content.length(), dataCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("文本文件上传失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取已上传的数据统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUploadStats() {
        Map<String, Object> stats = documentProcessingService.getUploadStats();
        return ResponseEntity.ok(stats);
    }
}
