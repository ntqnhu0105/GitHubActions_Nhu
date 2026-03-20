package com.hutech.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DebugController {

    @GetMapping("/debug/path")
    public String debugPath() {
        Path currentPath = Paths.get(".").toAbsolutePath();
        Path uploadsPath = currentPath.resolve("uploads");
        return "Current Working Dir: " + currentPath.toString() + 
               "<br>Uploads Path: " + uploadsPath.toString() +
               "<br>Uploads Exists: " + uploadsPath.toFile().exists() +
               "<br>Uploads is Dir: " + uploadsPath.toFile().isDirectory();
    }

    @SuppressWarnings("null")
    @GetMapping("/debug/image/{filename}")
    public ResponseEntity<Resource> debugImage(@PathVariable String filename) throws MalformedURLException {
        Path file = Paths.get("uploads").resolve(filename);
        java.net.URI uri = file.toUri();
        Resource resource = new UrlResource(uri);

        if (resource.exists() || resource.isReadable()) {
            MediaType mediaType = MediaType.IMAGE_JPEG;
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the file: " + file.toAbsolutePath());
        }
    }
}
