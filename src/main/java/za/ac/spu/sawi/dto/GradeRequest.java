package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class GradeRequest {

    private Integer studentId;

    private Integer assessmentId;

    @DecimalMin(value = "0.0", message = "Mark cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Mark cannot exceed 100")
    private Double mark;

    @Size(max = 500, message = "Feedback cannot exceed 500 characters")
    private String feedback;

    public GradeRequest() {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}