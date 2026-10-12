package za.ac.spu.sawi.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import za.ac.spu.sawi.dto.StudentSupportRequest;
import za.ac.spu.sawi.dto.StudentSupportResponse;

class StudentSupportServiceTest {

    private final StudentSupportService service =
            new StudentSupportService("", "https://api.openai.com", "gpt-4o-mini");

    @Test
    void stressedStudentIsDirectedToCounsellor() {
        StudentSupportResponse response = service.respond(
                new StudentSupportRequest("I feel stressed", "stressed", 5, false));

        assertTrue(response.consultationRecommendation().contains("counsellor"));
        assertTrue(response.response().contains("breathe in gently"));
        assertTrue(response.appointmentUrl().equals("/appointments"));
    }

    @Test
    void timeManagementConcernIsDirectedToAdvisor() {
        StudentSupportResponse response = service.respond(
                new StudentSupportRequest("I can't manage my time", "okay", 2, false));

        assertTrue(response.consultationRecommendation().contains("academic advisor"));
        assertTrue(response.response().contains("upcoming tasks"));
    }

    @Test
    void stressedTimeManagementConcernRecommendsBothSupports() {
        StudentSupportResponse response = service.respond(
                new StudentSupportRequest("I am stressed and can't manage my time",
                "overwhelmed", null, false));

        assertTrue(response.consultationRecommendation().contains("counsellor"));
        assertTrue(response.consultationRecommendation().contains("academic advisor"));
        assertTrue(response.response().contains("realistic schedule"));
    }
}