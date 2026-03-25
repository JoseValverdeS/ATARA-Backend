package com.atara.deb.ataraapi.dto.reporte;

import java.util.List;

public class ReporteEstudianteDto {

    private Long studentId;
    private String studentName;
    private String sectionName;
    private List<DetalleContenidoReporteDto> contenidos;

    public ReporteEstudianteDto() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<DetalleContenidoReporteDto> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<DetalleContenidoReporteDto> contenidos) {
        this.contenidos = contenidos;
    }
}