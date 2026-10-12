package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MoodCheckinRequest {

    @NotBlank(message = "Mood is required")
    @Size(max = 30, message = "Mood cannot exceed 30 characters")
    private String mood;

    @Min(value = 1, message = "Stress level must be at least 1")
    @Max(value = 5, message = "Stress level must not exceed 5")
    private Integer stressLevel;

    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 5, message = "Energy level must not exceed 5")
    private Integer energyLevel;

    @Min(value = 0, message = "Sleep hours cannot be negative")
    @Max(value = 24, message = "Sleep hours cannot exceed 24")
    private Double sleepHours;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public MoodCheckinRequest() {
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
}