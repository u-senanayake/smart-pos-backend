package lk.udcreations.file.controller;

import lk.udcreations.file.entity.Image;
import lk.udcreations.file.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImageService imageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/data/all")
    public ResponseEntity<List<Image>> getAll() {

        return ResponseEntity.ok(imageService.findAll());
    }

    @GetMapping("/data/{imageType}")
    public ResponseEntity<List<Image>> getImageDataByImageType(@PathVariable String imageType) {

        return ResponseEntity.ok(imageService.findByImageType(imageType));
    }

    @GetMapping("/data/{imageType}/{typeId}")
    public ResponseEntity<List<Image>> getImageDataByImageTypeAndTypeId(@PathVariable String imageType, @PathVariable Integer typeId) {

        return ResponseEntity.ok(imageService.findByImageTypeAndTypeId(imageType, typeId));
    }

    @GetMapping("/data/{imageType}/{typeId}/{imageId}")
    public ResponseEntity<Image> getImageDataByImageTypeAndTypeIdAndImageId(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable Integer imageId) {
        return ResponseEntity.ok(imageService.findByImageTypeAndTypeIdImageId(imageType, typeId, imageId));
    }

    @GetMapping("/resource/{imageType}/{typeId}/{imageId}")
    public Resource getImageResourceByImageTypeAndTypeIdAndImageId(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable Integer imageId) {
        return imageService.findByImageResourceTypeAndTypeIdImageId(imageType, typeId, imageId);
    }

    @PostMapping("/upload/{imageType}/{typeId}/{imageName}")
    public Image save(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable String imageName) {
        return imageService.save(imageType, typeId, imageName);
    }

    @GetMapping("/image-name/{imageType}/{typeId}")
    public String createFileName(@PathVariable String imageType, @PathVariable Integer typeId) {
        return imageService.createFileName(imageType, typeId);
    }
}
