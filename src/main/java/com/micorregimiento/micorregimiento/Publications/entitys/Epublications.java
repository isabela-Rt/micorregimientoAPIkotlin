package com.micorregimiento.micorregimiento.Publications.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Epublications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String contenido;

    @Column(name = "tipo_publicacion_id", nullable = false)
    private Long tipoPublicacionId;

    @Column(name = "fecha_Publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;


    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }
}
