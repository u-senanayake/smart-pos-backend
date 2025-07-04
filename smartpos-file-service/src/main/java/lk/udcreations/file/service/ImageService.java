package lk.udcreations.file.service;


import lk.udcreations.file.entity.Image;
import lk.udcreations.file.repository.ImageRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ImageService(ImageRepository imageRepository, ModelMapper modelMapper) {
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    public Image findByImageTypeAndTypeIdImageId(String imageType, Integer typeId, Integer imageId) {
        return imageRepository.findByImageTypeAndTypeIdAndImageId(imageType, typeId, imageId);
    }

    public List<Image> findByImageTypeAndTypeId(String imageType, Integer typeId) {
        return imageRepository.findByImageTypeAndTypeId(imageType, typeId);
    }

    public List<Image> findByImageType(String imageType) {
        return imageRepository.findByImageType(imageType);
    }

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public void save(String imageType, Integer typeId, String imageName) {

        LOGGER.info("Saving image with type: {}, typeId: {}", imageType, typeId);
        
        Image image = new Image();
        image.setImageType("product");
        image.setTypeId(typeId);
        image.setDescription(imageName);
        image.setImageAlt(imageName);
        image.setImageName(imageName);

        imageRepository.save(image);
    }

    public String createFileName(String imageType, Integer typeId) {

        List<Image> images = imageRepository.findByImageTypeAndTypeId(imageType, typeId);

        String imageName;

        if (images == null || images.isEmpty()) {
            imageName = imageType + typeId + "001";
        } else {
            Integer maxIncrement = imageRepository.findMaxIncrement(imageType, typeId);
            int nextIncrement = (maxIncrement == null ? 1 : maxIncrement + 1);
            String incrementStr = String.format("%03d", nextIncrement);
            imageName = imageType + typeId + incrementStr;
        }
        return imageName + ".jpg";
    }
}
