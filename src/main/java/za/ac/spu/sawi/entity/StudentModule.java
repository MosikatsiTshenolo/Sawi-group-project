package za.ac.spu.sawi.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "student_modules",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "module_id"})
    }
)
public class StudentModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_module_id")
    private Integer studentModuleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    public StudentModule() {
    }

    public Integer getStudentModuleId() {
        return studentModuleId;
    }

    public void setStudentModuleId(Integer studentModuleId) {
        this.studentModuleId = studentModuleId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
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

    @Override
    public String toString() {
        return "StudentModule{" +
                "studentModuleId=" + studentModuleId +
                ", academicYear=" + academicYear +
                ", semester='" + semester + '\'' +
                '}';
    }
}