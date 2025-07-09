package lk.udcreations.product.config;

import lk.udcreations.common.dto.file.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping("/api/v1/image/data/{imageType}/{typeId}")
    List<ImageDTO> getImageDataByImageTypeAndTypeId(@PathVariable String imageType, @PathVariable Integer typeId);
}
