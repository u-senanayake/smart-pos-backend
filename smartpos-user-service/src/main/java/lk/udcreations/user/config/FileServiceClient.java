package lk.udcreations.user.config;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import lk.udcreations.common.dto.file.ImageDTO;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping("/api/v1/image/data/{imageType}/{typeId}")
    List<ImageDTO> getImageDataByImageTypeAndTypeId(@PathVariable String imageType, @PathVariable Integer typeId);

    @PostMapping(value = "/api/v1/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImageDTO upload(@RequestPart("file") MultipartFile file, @RequestParam("imgType") String imgType,
                    @RequestParam("typeId") Integer typeId);
}