package com.micorregimiento.micorregimiento.Multimedia.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "multimedia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emultimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 300)
    private String url;

    @Column(name = "tipo_multimedia_id", nullable = false)
    private Long tipoMultimediaId;

    @Column(name = "publicacion_id", nullable = false)
    private Long publicacionId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
