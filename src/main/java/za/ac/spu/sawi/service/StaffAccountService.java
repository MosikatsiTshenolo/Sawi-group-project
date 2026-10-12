
package za.ac.spu.sawi.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.spu.sawi.entity.Advisor;
import za.ac.spu.sawi.entity.Counselor;
import za.ac.spu.sawi.entity.Lecturer;
import za.ac.spu.sawi.entity.Role;
import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.repository.AdvisorRepository;
import za.ac.spu.sawi.repository.CounselorRepository;
import za.ac.spu.sawi.repository.LecturerRepository;
import za.ac.spu.sawi.repository.RoleRepository;
import za.ac.spu.sawi.repository.UserRepository;

@Service
public class StaffAccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdvisorRepository advisorRepository;
    private final CounselorRepository counselorRepository;
    private final LecturerRepository lecturerRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffAccountService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AdvisorRepository advisorRepository,
            CounselorRepository counselorRepository,
            LecturerRepository lecturerRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.advisorRepository = advisorRepository;
        this.counselorRepository = counselorRepository;
        this.lecturerRepository = lecturerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createStaffAccount(
            String username,
            String password,
            String firstName,
            String lastName,
            String email,
            String phone,
            String roleName,
            String staffNumber,
            String faculty,
            String departmentOrSpecialization) {

        username = username == null ? "" : username.trim();
        email = email == null ? "" : email.trim();
        staffNumber = staffNumber == null ? "" : staffNumber.trim();
        faculty = faculty == null ? "" : faculty.trim();
        roleName = roleName == null ? "" : roleName.trim().toUpperCase();
        firstName = firstName == null ? "" : firstName.trim();
        lastName = lastName == null ? "" : lastName.trim();

        if (!roleName.equals("ADVISOR")
                && !roleName.equals("COUNSELOR")
                && !roleName.equals("LECTURER")
                && !roleName.equals("ADMIN")) {
            throw new IllegalArgumentException(
                    "Select a valid account role.");
        }

        if ((roleName.equals("ADVISOR")
                && !matchesChoice(faculty, Set.of(
                    "NAS", "EDUCATION", "HUMANITIES", "AGRICULTURE AND ECONOMICS")))
            || (roleName.equals("LECTURER")
                && !matchesChoice(faculty, Set.of(
                    "ICT301", "COMPUTER SCIENCE", "DATA SCIENCE",
                    "BED", "AGRICULTURE", "BCOM ACCOUNTING")))) {
            throw new IllegalArgumentException("Select a valid faculty for this staff role.");
        }

        if (username.isBlank() || password == null
                || password.length() < 8
                || firstName.isBlank() || lastName.isBlank()
                || email.isBlank()
                || (!roleName.equals("ADMIN") && staffNumber.isBlank())) {
            throw new IllegalArgumentException(
                    "Complete all required fields. Staff number is required for staff roles; password must be at least 8 characters.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "This username is already registered.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "This email address is already registered.");
        }

        if (roleName.equals("ADVISOR")
                && advisorRepository.existsByStaffNumber(staffNumber)) {
            throw new IllegalArgumentException(
                    "This staff number is already registered.");
        }

        if (roleName.equals("COUNSELOR")
                && counselorRepository.existsByStaffNumber(staffNumber)) {
            throw new IllegalArgumentException(
                    "This staff number is already registered.");
        }

        if (roleName.equals("LECTURER")
                && lecturerRepository.existsByStaffNumber(staffNumber)) {
            throw new IllegalArgumentException(
                    "This staff number is already registered.");
        }

        final String selectedRoleName = roleName;

        Role role = roleRepository.findByRoleName(selectedRoleName)
                .orElseThrow(() -> new IllegalStateException(
                        "The " + selectedRoleName
                                + " role is missing from the database."));
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone == null ? null : phone.trim());
        user.setRole(role);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        if (roleName.equals("ADVISOR")) {
            Advisor advisor = new Advisor();
            advisor.setUser(savedUser);
            advisor.setStaffNumber(staffNumber);
            advisor.setFaculty(faculty.toUpperCase());
            advisor.setDepartment(faculty.toUpperCase());
            advisorRepository.save(advisor);

        } else if (roleName.equals("COUNSELOR")) {
            Counselor counselor = new Counselor();
            counselor.setUser(savedUser);
            counselor.setStaffNumber(staffNumber);
            counselor.setSpecialization(departmentOrSpecialization);
            counselorRepository.save(counselor);

        } else if (roleName.equals("LECTURER")) {
            Lecturer lecturer = new Lecturer();
            lecturer.setUser(savedUser);
            lecturer.setStaffNumber(staffNumber);
            lecturer.setFaculty(faculty);
            lecturer.setDepartment(faculty);
            lecturerRepository.save(lecturer);
        }

        return savedUser;
    }

    private boolean matchesChoice(String value, Set<String> choices) {
        return choices.stream().anyMatch(choice -> choice.equalsIgnoreCase(value));
    }
}
