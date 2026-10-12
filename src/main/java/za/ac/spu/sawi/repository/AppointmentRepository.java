package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByStudent_StudentIdOrderByAppointmentDateAsc(
            Integer studentId);

    List<Appointment> findByAdvisor_AdvisorIdOrderByAppointmentDateAsc(
            Integer advisorId);

    List<Appointment> findByCounselor_CounselorIdOrderByAppointmentDateAsc(
            Integer counselorId);

    List<Appointment> findByAppointmentDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<Appointment> findByStatus(String status);

        List<Appointment> findByStatusAndCompletedAtBetween(
                        String status,
                        LocalDateTime startDate,
                        LocalDateTime endDate);
}