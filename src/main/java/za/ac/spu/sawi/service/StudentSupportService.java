package za.ac.spu.sawi.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import za.ac.spu.sawi.dto.StudentSupportRequest;
import za.ac.spu.sawi.dto.StudentSupportResponse;

@Service
public class StudentSupportService {

    private static final String SYSTEM_PROMPT = "You are Intelligent Triage, a supportive university student assistant. "
            + "Answer the student's question clearly and concisely, including ordinary academic and campus-life questions. "
            + "Do not diagnose, prescribe medication, claim to replace a counsellor, or promise confidentiality. "
            + "For immediate danger, self-harm, or suicidal thoughts, encourage contacting emergency services and campus support now. "
            + "Use a calm, respectful tone and provide one practical next step.";

    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public StudentSupportService(
            @Value("${app.ai.openai.api-key:}") String apiKey,
            @Value("${app.ai.openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${app.ai.openai.model:gpt-4o-mini}") String model) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public StudentSupportResponse respond(StudentSupportRequest request) {
        String recommendation = recommendConsultation(
                request.message(), request.mood(), request.stressLevel());
        String safetyResponse = crisisResponse(request.message());
        if (safetyResponse != null) {
            return response(safetyResponse, recommendation, false);
        }

        if (request.allowExternalAi() && apiKey != null && !apiKey.isBlank()) {
            try {
                String answer = askOpenAi(request);
                if (answer != null && !answer.isBlank()) {
                    return response(answer, recommendation, true);
                }
            } catch (RestClientException exception) {
                return response(
                        "I couldn't reach the AI service right now. "
                                + localSuggestion(request.message(), request.mood(),
                                        request.stressLevel()),
                        recommendation,
                        false);
            }
        }

        return response(
                localSuggestion(request.message(), request.mood(), request.stressLevel()),
                recommendation,
                false);
    }

    private String askOpenAi(StudentSupportRequest request) {
        String context = "Student message: " + request.message()
                + "\nSelected mood: " + safeValue(request.mood())
                + "\nSelected stress level (1-5): "
                + (request.stressLevel() == null ? "not selected" : request.stressLevel());
        Map<?, ?> result = RestClient.create(baseUrl)
                .post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(Map.of(
                        "model", model,
                        "messages", List.of(
                                Map.of("role", "system", "content", SYSTEM_PROMPT),
                                Map.of("role", "user", "content", context)),
                        "temperature", 0.4))
                .retrieve()
                .body(Map.class);
        if (result == null || !(result.get("choices") instanceof List<?> choices)
                || choices.isEmpty() || !(choices.get(0) instanceof Map<?, ?> choice)
                || !(choice.get("message") instanceof Map<?, ?> message)) {
            return null;
        }
        Object content = message.get("content");
        return content instanceof String answer ? answer.trim() : null;
    }

    private String recommendConsultation(String message, String mood, Integer stressLevel) {
        String text = ((message == null ? "" : message) + " "
                + (mood == null ? "" : mood)).toLowerCase(Locale.ROOT);
        boolean wellbeingConcern = isWellbeingConcern(text, stressLevel);
        boolean timeManagementConcern = isTimeManagementConcern(text);
        boolean academicConcern = timeManagementConcern
            || containsAny(text, "assignment", "exam", "assessment", "grade",
                "mark", "module", "course", "lecturer", "study", "deadline");

        if (wellbeingConcern && academicConcern) {
            return "A counsellor is a good first contact for the stress you're feeling. An academic advisor can also help you plan your study time and deadlines.";
        }
        if (wellbeingConcern) {
            return "A counsellor is the best first contact for this wellbeing concern.";
        }
        if (academicConcern) {
            return "An academic advisor is the best first contact for this academic question.";
        }
        return "If this is about wellbeing, contact a counsellor; if it is about studies or your course, contact an academic advisor.";
    }

    private String localSuggestion(String message, String mood, Integer stressLevel) {
        String text = (message + " " + safeValue(mood)).toLowerCase(Locale.ROOT);
        if (isTimeManagementConcern(text) && isWellbeingConcern(text, stressLevel)) {
            return "That sounds like a lot to carry. A counsellor can support the stress, and an academic advisor can help make a realistic schedule. For now, list your next three deadlines and choose one 20-minute task to start with.";
        }
        if (isTimeManagementConcern(text)) {
            return "An academic advisor can help you plan study time and deadlines. For now, list your upcoming tasks, mark their due dates, estimate how long each will take, then schedule one short block for the nearest task.";
        }
        if (isWellbeingConcern(text, stressLevel)) {
            return "A counsellor can help you work through this stress. For the next minute, breathe in gently for 4 counts and out for 6, then choose one small next step.";
        }
        if (containsAny(text, "sad", "lonely", "down", "grief")) {
            return "I'm sorry this feels difficult. Try naming five things you can see and four sensations you can feel, and consider contacting someone you trust. "
                    + "You can book the recommended support below.";
        }
        return "I can help with general questions when the AI service is enabled. For now, tell me a little more or use the recommendation below to choose the right support.";
    }

    private String crisisResponse(String message) {
        String text = message == null ? "" : message.toLowerCase(Locale.ROOT);
        if (containsAny(text, "suicid", "kill myself", "self-harm", "self harm",
                "hurt myself", "can't stay safe", "cannot stay safe")) {
            return "I'm sorry you're facing this. If you may be in immediate danger, call 112 in South Africa now, contact campus support, or go to a trusted person nearby. This chat is not emergency care.";
        }
        return null;
    }

    private boolean isTimeManagementConcern(String text) {
        return containsAny(text, "manage my time", "manage time", "time management",
                "organize my time", "organise my time", "can't manage", "cannot manage",
                "cant manage", "behind on", "keep up with deadlines", "plan my week");
    }

    private boolean isWellbeingConcern(String text, Integer stressLevel) {
        return (stressLevel != null && stressLevel >= 4)
                || containsAny(text, "stress", "stressed", "stressor", "sad", "anxious",
                        "anxiety", "overwhelmed", "panic", "lonely", "grief",
                        "depressed", "unsafe", "burnt out", "burned out");
    }

    private StudentSupportResponse response(
            String answer, String recommendation, boolean externalAiUsed) {
        return new StudentSupportResponse(
                answer + "\n\n" + recommendation,
                recommendation,
                "/appointments",
                externalAiUsed);
    }

    private boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "not selected" : value;
    }
}