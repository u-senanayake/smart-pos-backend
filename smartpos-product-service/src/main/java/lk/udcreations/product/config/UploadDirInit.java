package lk.udcreations.product.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class UploadDirInit {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Upload directory created at: " + uploadDir);
        }
    }
}
