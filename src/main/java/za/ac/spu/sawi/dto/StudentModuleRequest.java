package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StudentModuleRequest {

    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotNull(message = "Module ID is required")
    private Integer moduleId;

    @NotNull(message = "Academic year is required")
    @Min(value = 2000, message = "Academic year must be valid")
    @Max(value = 2100, message = "Academic year must be valid")
    private Integer academicYear;

    @NotBlank(message = "Semester is required")
    @Size(max = 20, message = "Semester cannot exceed 20 characters")
    private String semester;

    public StudentModuleRequest() {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}