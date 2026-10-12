package za.ac.spu.sawi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "advisors")
public class Advisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advisor_id")
    private Integer advisorId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "staff_number", nullable = false, unique = true, length = 20)
    private String staffNumber;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "faculty", length = 100)
    private String faculty;

    public Advisor() {
    }

    public Integer getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(Integer advisorId) {
        this.advisorId = advisorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Advisor{" +
                "advisorId=" + advisorId +
                ", staffNumber='" + staffNumber + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}