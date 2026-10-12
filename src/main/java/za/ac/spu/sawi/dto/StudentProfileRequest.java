package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudentProfileRequest {

    @NotBlank(message = "Student number is required")
    @Size(max = 20, message = "Student number cannot exceed 20 characters")
    private String studentNumber;

    @Size(max = 100, message = "Course cannot exceed 100 characters")
    private String course;

    private Integer yearOfStudy;

    public StudentProfileRequest() {
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}