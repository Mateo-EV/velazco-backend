package com.velazco.velazco_backend.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String store(MultipartFile image);

    void delete(String imagePath);

    void validateSize(MultipartFile image);
}
