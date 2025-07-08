package lk.udcreations.file.service;


import lk.udcreations.file.entity.Image;
import lk.udcreations.file.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image findByImageTypeAndTypeIdImageId(String imageType, Integer typeId, Integer imageId) {
        LOGGER.info("Finding image with type: {}, typeId: {}, imageId: {}", imageType, typeId, imageId);
        return imageRepository.findByImageTypeAndTypeIdAndImageId(imageType, typeId, imageId);
    }

    public Resource findByImageResourceTypeAndTypeIdImageId(String imageType, Integer typeId, Integer imageId) {

        LOGGER.info("Loading image resource with type: {}, typeId: {}, imageId: {}", imageType, typeId, imageId);
        try {
            Image image = imageRepository.findByImageTypeAndTypeIdAndImageId(imageType, typeId, imageId);

            Path root = Paths.get(uploadDir);
            Path file = root.resolve(image.getImageName());
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                LOGGER.error("Could not load the file: {}, typeId: {}, imageId: {}", imageType, typeId, imageId);
                throw new RuntimeException("Could not load, file does not exist!");
            } else {
                LOGGER.info("Successfully loaded the file: {}, typeId: {}, imageId: {}", imageType, typeId, imageId);
                return resource;
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL exception while loading the file: {}, typeId: {}, imageId: {}", imageType, typeId, imageId, e);
            throw new RuntimeException("Could not load the file. Error: " + e.getMessage(), e);
        }
    }

    public List<Image> findByImageTypeAndTypeId(String imageType, Integer typeId) {
        LOGGER.info("Finding images with type: {}, typeId: {}", imageType, typeId);
        return imageRepository.findByImageTypeAndTypeId(imageType, typeId);
    }

    public List<Image> findByImageType(String imageType) {
        LOGGER.info("Finding images with type: {}", imageType);
        return imageRepository.findByImageType(imageType);
    }

    public List<Image> findAll() {
        LOGGER.info("Retrieving all images");
        return imageRepository.findAll();
    }

    public Image save(String imageType, Integer typeId, String imageName) {

        LOGGER.info("Saving image with type: {}, typeId: {}", imageType, typeId);

        Image image = new Image();
        image.setImageType("product");
        image.setTypeId(typeId);
        image.setDescription(imageName);
        image.setImageAlt(imageName);
        image.setImageName(imageName + ".jpg");
        image.setImageSequence(imageName);

        return imageRepository.save(image);
    }

    public String getImageSequence(String imageType, Integer typeId) {

        List<Image> images = imageRepository.findByImageTypeAndTypeId(imageType, typeId);

        String imageSequence;

        if (images == null || images.isEmpty()) {
            imageSequence = imageType + typeId + "001";
        } else {
            Integer maxIncrement = imageRepository.findMaxIncrement(imageType, typeId);
            int nextIncrement = (maxIncrement == null ? 1 : maxIncrement + 1);
            String incrementStr = String.format("%03d", nextIncrement);
            imageSequence = imageType + typeId + incrementStr;
        }
        return imageSequence;
    }
}
