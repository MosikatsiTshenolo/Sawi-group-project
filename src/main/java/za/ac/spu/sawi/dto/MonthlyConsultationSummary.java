package za.ac.spu.sawi.dto;

public record MonthlyConsultationSummary(
        String staffName,
        String staffRole,
        long studentsConsulted,
        long consultationCount) {
}