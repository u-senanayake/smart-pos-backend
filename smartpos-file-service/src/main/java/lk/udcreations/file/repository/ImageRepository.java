package lk.udcreations.file.repository;

import lk.udcreations.file.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Image findByImageTypeAndTypeIdAndImageId(String imageType, Integer id, Integer imageId);

    List<Image> findByImageTypeAndTypeId(String imageType, Integer id);

    List<Image> findByImageType(String imageType);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.imageSequence, LENGTH(i.imageType) + LENGTH(CAST(i.typeId AS string)) + 1) AS int)), 0) " +
            "FROM Image i WHERE i.imageType = :imageType AND i.typeId = :typeId")
    Integer findMaxIncrement(@Param("imageType") String imageType, @Param("typeId") Integer typeId);
}
