package za.ac.spu.sawi.dto;

import za.ac.spu.sawi.entity.Assessment;

public record AssessmentProgress(
        Assessment assessment,
        long enrolledCount,
        long submittedCount,
        long outstandingCount,
        long daysRemaining) {
}