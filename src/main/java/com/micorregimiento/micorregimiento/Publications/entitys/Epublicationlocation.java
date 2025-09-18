package com.micorregimiento.micorregimiento.Publications.entitys;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "publicacion_ubicacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Epublicationlocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publicacion_id", nullable = false)
    private Long publicacionId;

    @Column(name = "barrio_id")
    private Long barrioId; // aceptar null

    @Column(name = "corregimiento_id", nullable = false)
    private Long corregimientoId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}