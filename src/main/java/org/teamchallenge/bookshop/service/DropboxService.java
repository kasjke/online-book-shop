package org.teamchallenge.bookshop.service;


import org.springframework.web.multipart.MultipartFile;




public interface DropboxService {
    void createFolder(String path);

     String uploadImage(String path, MultipartFile file);
}
