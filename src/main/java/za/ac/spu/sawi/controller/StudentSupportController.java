package za.ac.spu.sawi.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import za.ac.spu.sawi.dto.StudentSupportRequest;
import za.ac.spu.sawi.dto.StudentSupportResponse;
import za.ac.spu.sawi.service.StudentSupportService;

@RestController
@RequestMapping("/api/student-support")
public class StudentSupportController {

    private final StudentSupportService studentSupportService;

    public StudentSupportController(StudentSupportService studentSupportService) {
        this.studentSupportService = studentSupportService;
    }

    @PostMapping("/chat")
    public StudentSupportResponse chat(@Valid @RequestBody StudentSupportRequest request) {
        if (request.message().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a message to continue.");
        }
        return studentSupportService.respond(request);
    }
}