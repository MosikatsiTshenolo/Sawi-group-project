package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Advisor;
import za.ac.spu.sawi.repository.AdvisorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdvisorService {

    private final AdvisorRepository advisorRepository;

    public AdvisorService(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }

    public List<Advisor> findAll() {
        return advisorRepository.findAll();
    }

    public Optional<Advisor> findById(Integer advisorId) {
        return advisorRepository.findById(advisorId);
    }

    public Optional<Advisor> findByStaffNumber(String staffNumber) {
        return advisorRepository.findByStaffNumber(staffNumber);
    }

    public Optional<Advisor> findByUserId(Integer userId) {
        return advisorRepository.findByUser_UserId(userId);
    }

    public boolean existsByStaffNumber(String staffNumber) {
        return advisorRepository.existsByStaffNumber(staffNumber);
    }

    public Advisor save(Advisor advisor) {
        return advisorRepository.save(advisor);
    }

    public void deleteById(Integer advisorId) {
        advisorRepository.deleteById(advisorId);
    }
}