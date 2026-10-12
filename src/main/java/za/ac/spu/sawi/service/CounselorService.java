package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Counselor;
import za.ac.spu.sawi.repository.CounselorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CounselorService {

    private final CounselorRepository counselorRepository;

    public CounselorService(CounselorRepository counselorRepository) {
        this.counselorRepository = counselorRepository;
    }

    public List<Counselor> findAll() {
        return counselorRepository.findAll();
    }

    public Optional<Counselor> findById(Integer counselorId) {
        return counselorRepository.findById(counselorId);
    }

    public Optional<Counselor> findByStaffNumber(String staffNumber) {
        return counselorRepository.findByStaffNumber(staffNumber);
    }

    public Optional<Counselor> findByUserId(Integer userId) {
        return counselorRepository.findByUser_UserId(userId);
    }

    public boolean existsByStaffNumber(String staffNumber) {
        return counselorRepository.existsByStaffNumber(staffNumber);
    }

    public Counselor save(Counselor counselor) {
        return counselorRepository.save(counselor);
    }

    public void deleteById(Integer counselorId) {
        counselorRepository.deleteById(counselorId);
    }
}