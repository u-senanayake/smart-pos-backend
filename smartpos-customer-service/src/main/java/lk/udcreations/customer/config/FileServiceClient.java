package lk.udcreations.customer.config;

import lk.udcreations.common.dto.file.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping("/api/v1/image/data/{imageType}/{typeId}")
    List<ImageDTO> getImageDataByImageTypeAndTypeId(@PathVariable String imageType, @PathVariable Integer typeId);

    @PostMapping(value = "/api/v1/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImageDTO upload(@RequestPart("file") MultipartFile file, @RequestParam("imgType") String imgType,
                    @RequestParam("typeId") Integer typeId);
}
