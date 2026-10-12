package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentSupportRequest(
        @NotBlank @Size(max = 1000) String message,
        @Size(max = 30) String mood,
        @Min(1) @Max(5) Integer stressLevel,
        boolean allowExternalAi) {
}