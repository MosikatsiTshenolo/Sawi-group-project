
package za.ac.spu.sawi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Notification title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Notification message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;

    @Size(max = 50, message = "Notification type cannot exceed 50 characters")
    private String notificationType;

    public NotificationRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
