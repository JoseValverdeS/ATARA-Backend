package com.atara.deb.ataraapi.model;

import com.atara.deb.ataraapi.model.enums.EstadoEstudiante;
import com.atara.deb.ataraapi.model.enums.Genero;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Código institucional o cédula — UNIQUE NOT NULL
    @Column(name = "identificacion", nullable = false, length = 25, unique = true)
    private String identificacion;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido1", nullable = false, length = 100)
    private String apellido1;

    @Column(name = "apellido2", length = 100)
    private String apellido2;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // CHAR(1) CHECK (genero IN ('M','F','O'))
    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 1)
    private Genero genero;

    @Column(name = "nombre_acudiente", length = 200)
    private String nombreAcudiente;

    @Column(name = "telefono_acudiente", length = 20)
    private String telefonoAcudiente;

    @Column(name = "correo_acudiente", length = 150)
    private String correoAcudiente;

    // CHECK (estado IN ('ACTIVO','INACTIVO','TRASLADADO'))
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoEstudiante estado;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
