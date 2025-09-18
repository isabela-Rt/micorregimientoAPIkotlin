package com.micorregimiento.micorregimiento.Roles.entitys;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol_privilegios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol_id", nullable = false)
    private Long rolId;

    @Column(name = "privilegio_id", nullable = false)
    private Long privilegioId;
}
