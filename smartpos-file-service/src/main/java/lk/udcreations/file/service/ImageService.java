package lk.udcreations.file.service;


import lk.udcreations.common.dto.file.ImageDTO;
import lk.udcreations.file.entity.Image;
import lk.udcreations.file.repository.ImageRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    List<String> IMG_TYPES = Arrays.asList("product", "category", "brand", "user", "sale");

    public ImageService(ImageRepository imageRepository, ModelMapper modelMapper) {
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    public ImageDTO findByImageTypeAndTypeIdImageId(String imageType, Integer typeId, Integer imageId) {
        LOGGER.info("Finding image with type: {}, typeId: {}, imageId: {}", imageType, typeId, imageId);
        Image image = imageRepository.findByImageTypeAndTypeIdAndImageId(imageType, typeId, imageId);

        return convertToDTO(image);
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

    public List<ImageDTO> findByImageTypeAndTypeId(String imageType, Integer typeId) {
        LOGGER.info("Finding images with type: {}, typeId: {}", imageType, typeId);
        List<Image> images = imageRepository.findByImageTypeAndTypeId(imageType, typeId);

        return images.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<Image> findByImageType(String imageType) {
        LOGGER.info("Finding images with type: {}", imageType);
        return imageRepository.findByImageType(imageType);
    }

    public List<Image> findAll() {
        LOGGER.info("Retrieving all images");
        return imageRepository.findAll();
    }

    public ImageDTO save(String imageType, Integer typeId, String imageName) {

        LOGGER.info("Saving image with type: {}, typeId: {}", imageType, typeId);

        Image image = new Image();
        image.setImageType("product");
        image.setTypeId(typeId);
        image.setDescription(imageName);
        image.setImageAlt(imageName);
        image.setImageName(imageName + ".jpg");
        image.setImageSequence(imageName);

        return convertToDTO(imageRepository.save(image));
    }

    public ImageDTO upload(MultipartFile file, String imgType, Integer typeId) {

        if (!IMG_TYPES.contains(imgType)) {
            throw new IllegalArgumentException("Invalid image type");
        }
        String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        Image image;
        try {
            String imageSequence = getImageSequence(imgType, typeId);
            String fileName = imageSequence + ".jpg";
            String filePath = uploadDir + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            image = new Image();
            image.setImageType(imgType);
            image.setTypeId(typeId);
            image.setDescription("Image for " + imgType + " with ID " + typeId);
            image.setImageAlt("Image for " + imgType + " with ID " + typeId);
            image.setImageName(fileName);
            image.setImageSequence(imageSequence);
        } catch (Exception e) {
            LOGGER.error("Error while parsing product JSON or uploading file: {}", e.getMessage());
            throw new RuntimeException("Invalid product data or file upload failed.");
        }
        return convertToDTO(imageRepository.save(image));
    }

    public void deleteImage(Integer imageId) {
        LOGGER.info("Deleting image with ID: {}", imageId);
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        File file = new File(uploadDir + image.getImageName());
        if (file.exists()) {
            if (file.delete()) {
                LOGGER.info("Successfully deleted file: {}", file.getAbsolutePath());
            } else {
                LOGGER.error("Failed to delete file: {}", file.getAbsolutePath());
            }
        } else {
            LOGGER.warn("File does not exist: {}", file.getAbsolutePath());
        }
        imageRepository.deleteById(imageId);
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

    private ImageDTO convertToDTO(Image image) {
        return modelMapper.map(image, ImageDTO.class);
    }
}
