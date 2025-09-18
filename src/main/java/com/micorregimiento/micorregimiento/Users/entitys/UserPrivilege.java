package com.micorregimiento.micorregimiento.Users.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_privilegios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "privilegio_id", nullable = false)
    private Long privilegioId;
}
