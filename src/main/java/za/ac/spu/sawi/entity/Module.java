package za.ac.spu.sawi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "module_code", nullable = false, unique = true, length = 20)
    private String moduleCode;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "credits")
    private Integer credits;

    @ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "lecturer_modules",
    joinColumns = @JoinColumn(name = "module_id"),
    inverseJoinColumns = @JoinColumn(name = "lecturer_id")
)
private java.util.Set<Lecturer> lecturers = new java.util.HashSet<>();

    public Module() {
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    
    public java.util.Set<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(java.util.Set<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }


    @Override
    public String toString() {
        return "Module{" +
                "moduleId=" + moduleId +
                ", moduleCode='" + moduleCode + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", credits=" + credits +
                '}';
    }
}