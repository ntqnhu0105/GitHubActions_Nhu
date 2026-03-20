package com.hutech.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Thư mục lưu ảnh upload: nằm ngoài target để ảnh không mất khi build lại. */
    public static final String UPLOAD_DIR = resolveUploadDir();

    private static String resolveUploadDir() {
        Path dir = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (Exception ignored) { }
        return dir.toString();
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        String location = uploadDir.toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations(location);
        // Fallback: ảnh tĩnh trong static/images (nếu có)
        registry.addResourceHandler("/static-images/**")
                .addResourceLocations("classpath:/static/images/");
        // Serve frontend QLS (AJAX management page)
        registry.addResourceHandler("/qls/**")
                .addResourceLocations("classpath:/QLS/");
    }
}
