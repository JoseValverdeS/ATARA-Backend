package com.atara.deb.ataraapi.dto.reporte;

import java.util.List;

public class ReporteSeccionDto {

    private Long sectionId;
    private String sectionName;
    private String subjectName;
    private String periodName;
    private Integer totalStudents;
    private List<ReporteEstudianteDto> estudiantes;

    public ReporteSeccionDto() {
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public List<ReporteEstudianteDto> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<ReporteEstudianteDto> estudiantes) {
        this.estudiantes = estudiantes;
    }
}