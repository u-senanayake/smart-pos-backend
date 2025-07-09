package lk.udcreations.file.controller;

import lk.udcreations.file.entity.Image;
import lk.udcreations.file.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/data/all")
    public ResponseEntity<List<Image>> getAll() {
        return ResponseEntity.ok(imageService.findAll());
    }

    @GetMapping("/data/{imageType}")
    public ResponseEntity<List<Image>> getImageDataByImageType(
            @PathVariable String imageType) {
        return ResponseEntity.ok(imageService.findByImageType(imageType));
    }

    @GetMapping("/data/{imageType}/{typeId}")
    public ResponseEntity<List<Image>> getImageDataByImageTypeAndTypeId(
            @PathVariable String imageType,
            @PathVariable Integer typeId) {
        return ResponseEntity.ok(imageService.findByImageTypeAndTypeId(imageType, typeId));
    }

    @GetMapping("/data/{imageType}/{typeId}/{imageId}")
    public ResponseEntity<Image> getImageDataByImageTypeAndTypeIdAndImageId(
            @PathVariable String imageType,
            @PathVariable Integer typeId,
            @PathVariable Integer imageId) {
        return ResponseEntity.ok(imageService.findByImageTypeAndTypeIdImageId(imageType, typeId, imageId));
    }

    @GetMapping("/resource/{imageType}/{typeId}/{imageId}")
    public Resource getImageResourceByImageTypeAndTypeIdAndImageId(
            @PathVariable String imageType,
            @PathVariable Integer typeId,
            @PathVariable Integer imageId) {
        return imageService.findByImageResourceTypeAndTypeIdImageId(imageType, typeId, imageId);
    }

    @PostMapping("/save/{imageType}/{typeId}/{imageName}")
    public Image save(
            @PathVariable String imageType,
            @PathVariable Integer typeId,
            @PathVariable String imageName) {
        return imageService.save(imageType, typeId, imageName);
    }

    @PostMapping("/upload")
    public Image upload(@RequestParam("file") MultipartFile file,
                        @RequestParam("imgType") String imgType,
                        @RequestParam("typeId") Integer typeId) {
        return imageService.upload(file, imgType, typeId);
    }

    @DeleteMapping("/delete/{imageId}")
    public void deleteImage(@PathVariable Integer imageId) {
        imageService.deleteImage(imageId);
    }
    
    @GetMapping("/image-name/{imageType}/{typeId}")
    public String getImageSequence(
            @PathVariable String imageType,
            @PathVariable Integer typeId) {
        return imageService.getImageSequence(imageType, typeId);
    }
}
