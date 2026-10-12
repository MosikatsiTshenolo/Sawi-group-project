package za.ac.spu.sawi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_checkins")
public class MoodCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_id")
    private Integer checkinId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "mood", nullable = false, length = 30)
    private String mood;

    @Column(name = "stress_level")
    private Integer stressLevel;

    @Column(name = "energy_level")
    private Integer energyLevel;

    @Column(name = "sleep_hours")
    private Double sleepHours;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "checkin_date", nullable = false)
    private LocalDateTime checkinDate;

    public MoodCheckin() {
    }

    public Integer getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(Integer checkinId) {
        this.checkinId = checkinId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

    @PrePersist
    protected void onCreate() {
        if (checkinDate == null) {
            checkinDate = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "MoodCheckin{" +
                "checkinId=" + checkinId +
                ", mood='" + mood + '\'' +
                ", stressLevel=" + stressLevel +
                ", energyLevel=" + energyLevel +
                ", sleepHours=" + sleepHours +
                ", checkinDate=" + checkinDate +
                '}';
    }
}