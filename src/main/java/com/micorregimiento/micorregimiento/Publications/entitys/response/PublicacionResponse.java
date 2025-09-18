package com.micorregimiento.micorregimiento.Publications.entitys.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionResponse {
    private Long id;
    private String titulo;
    private String contenido;
    private Long tipoPublicacionId;
    private String tipoPublicacionNombre;
    private LocalDateTime fechaPublicacion;
    private Long autorId;
    private String autorNombre;
    private List<Long> barrioIds;
    private List<Long> corregimientoIds;
    private boolean exitoso;
    private String mensaje;
}
