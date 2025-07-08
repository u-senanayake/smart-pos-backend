package lk.udcreations.file.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "image")
public class Image {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @NotBlank(message = "Image type cannot be blank")
    @Size(max = 20, message = "Image type must be less than 20 characters")
    @Column(name = "image_type")
    private String imageType;

    @Column(name = "type_id")
    private Integer typeId;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 100, message = "Image type must be less than 20 characters")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "Image alt cannot be blank")
    @Size(max = 20, message = "Image alt must be less than 20 characters")
    @Column(name = "image_alt")
    private String imageAlt;

    @NotBlank(message = "Image name cannot be blank")
    @Size(max = 20, message = "Image alt must be less than 20 characters")
    @Column(name = "image_name")
    private String imageName;

    @NotBlank(message = "Image sequence cannot be blank")
    @Size(max = 3, message = "Image sequence must be less than 3 characters")
    @Column(name = "image_sequence")
    private String imageSequence;
}
