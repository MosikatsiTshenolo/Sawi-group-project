package za.ac.spu.sawi.dto;

public record StudentSupportResponse(
        String response,
        String consultationRecommendation,
        String appointmentUrl,
        boolean externalAiUsed) {
}