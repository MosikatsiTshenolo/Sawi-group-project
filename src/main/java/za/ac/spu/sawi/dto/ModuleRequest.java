package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ModuleRequest {

    @NotBlank(message = "Module code is required")
    @Size(max = 20, message = "Module code cannot exceed 20 characters")
    private String moduleCode;

    @NotBlank(message = "Module name is required")
    @Size(max = 100, message = "Module name cannot exceed 100 characters")
    private String moduleName;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Min(value = 0, message = "Credits cannot be negative")
    private Integer credits;

    public ModuleRequest() {
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
}