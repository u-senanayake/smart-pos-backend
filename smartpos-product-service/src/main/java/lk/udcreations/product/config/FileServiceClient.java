package lk.udcreations.product.config;

import lk.udcreations.product.entity.Image;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping("/api/v1/image/data/{imageType}/{typeId}")
    public List<Image> getImageDataByImageTypeAndTypeId(@PathVariable String imageType, @PathVariable Integer typeId);

    @GetMapping("/api/v1/image/data/{imageType}/{typeId}/{imageId}")
    public Image getImageDataByImageTypeAndTypeIdAndImageId(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable Integer imageId);

    @GetMapping("/api/v1/image/resource/{imageType}/{typeId}/{imageId}")
    public Resource getImageResourceByImageTypeAndTypeIdAndImageId(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable Integer imageId);

    @PostMapping("/api/v1/image/upload/{imageType}/{typeId}/{imageName}")
    public void save(@PathVariable String imageType, @PathVariable Integer typeId, @PathVariable String imageName);

    @GetMapping("/api/v1/image/image-name/{imageType}/{typeId}")
    public String createFileName(@PathVariable String imageType, @PathVariable Integer typeId);
}
