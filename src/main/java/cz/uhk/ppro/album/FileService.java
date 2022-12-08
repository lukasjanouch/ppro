package cz.uhk.ppro.album;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<String> upload(List<MultipartFile> images);
}
