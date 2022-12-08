package cz.uhk.ppro.album;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public List<String> upload(List<MultipartFile> images) {
        List<String> imagesStr = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
            Path path = Paths.get("C:/Users/Owner/Desktop/Aplikovana-informatika/PPRO/ppro/images/"
                    + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imagesStr.add(image.getOriginalFilename());
        }
        return imagesStr;
    }
}
