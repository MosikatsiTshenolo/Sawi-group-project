package za.ac.spu.sawi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "counselors")
public class Counselor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "counselor_id")
    private Integer counselorId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "staff_number", nullable = false, unique = true, length = 20)
    private String staffNumber;

    @Column(name = "specialization", length = 100)
    private String specialization;

    public Counselor() {
    }

    public Integer getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(Integer counselorId) {
        this.counselorId = counselorId;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Counselor{" +
                "counselorId=" + counselorId +
                ", staffNumber='" + staffNumber + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}