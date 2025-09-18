package com.micorregimiento.micorregimiento.Publications.entitys.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublicacionRequest {
    private String titulo;
    private String contenido;
    private Long tipoPublicacionId;
    private List<Long> barrioIds;
    private List<Long> corregimientoIds;
}
