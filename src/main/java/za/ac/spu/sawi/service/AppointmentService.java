package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Appointment;
import za.ac.spu.sawi.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findById(Integer appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> findByStudentId(Integer studentId) {
        return appointmentRepository
                .findByStudent_StudentIdOrderByAppointmentDateAsc(studentId);
    }

    public List<Appointment> findByAdvisorId(Integer advisorId) {
        return appointmentRepository
                .findByAdvisor_AdvisorIdOrderByAppointmentDateAsc(advisorId);
    }

    public List<Appointment> findByCounselorId(Integer counselorId) {
        return appointmentRepository
                .findByCounselor_CounselorIdOrderByAppointmentDateAsc(counselorId);
    }

    public List<Appointment> findByDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return appointmentRepository.findByAppointmentDateBetween(
                startDate, endDate);
    }

    public List<Appointment> findByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteById(Integer appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}