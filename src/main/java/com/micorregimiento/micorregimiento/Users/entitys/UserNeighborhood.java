package com.micorregimiento.micorregimiento.Users.entitys;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario_barrios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNeighborhood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "barrio_id", nullable = false)
    private Long barrioId;
}
