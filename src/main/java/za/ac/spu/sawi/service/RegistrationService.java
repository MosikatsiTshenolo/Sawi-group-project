
package za.ac.spu.sawi.service;

import java.time.Year;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.spu.sawi.dto.RegisterRequest;
import za.ac.spu.sawi.entity.Advisor;
import za.ac.spu.sawi.entity.Counselor;
import za.ac.spu.sawi.entity.Lecturer;
import za.ac.spu.sawi.entity.Module;
import za.ac.spu.sawi.entity.Role;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.entity.StudentModule;
import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.repository.AdvisorRepository;
import za.ac.spu.sawi.repository.CounselorRepository;
import za.ac.spu.sawi.repository.LecturerRepository;
import za.ac.spu.sawi.repository.ModuleRepository;
import za.ac.spu.sawi.repository.RoleRepository;
import za.ac.spu.sawi.repository.StudentRepository;
import za.ac.spu.sawi.repository.StudentModuleRepository;
import za.ac.spu.sawi.repository.UserRepository;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
        private final StudentModuleRepository studentModuleRepository;
        private final ModuleRepository moduleRepository;
        private final AdvisorRepository advisorRepository;
        private final CounselorRepository counselorRepository;
        private final LecturerRepository lecturerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository,
            StudentModuleRepository studentModuleRepository,
            ModuleRepository moduleRepository,
            AdvisorRepository advisorRepository,
            CounselorRepository counselorRepository,
            LecturerRepository lecturerRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.studentModuleRepository = studentModuleRepository;
        this.moduleRepository = moduleRepository;
        this.advisorRepository = advisorRepository;
        this.counselorRepository = counselorRepository;
        this.lecturerRepository = lecturerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername().trim())) {
            throw new IllegalArgumentException(
                    "This username is already registered.");
        }

        if (userRepository.existsByEmail(
                request.getEmail().trim())) {
            throw new IllegalArgumentException(
                    "This email address is already registered.");
        }

        String requestedRole =
                request.getRoleName().trim().toUpperCase();

        if (requestedRole.equals("ADMIN")) {
            throw new IllegalArgumentException(
                    "Admin accounts can only be created by an existing administrator.");
        }

        if (!requestedRole.equals("STUDENT")) {
            throw new IllegalArgumentException(
                "Lecturer, advisor, and counsellor profiles can only be created by an administrator. Select Student or contact the administrator.");
        }

        String faculty = trimToEmpty(request.getFaculty());
        if (requestedRole.equals("STUDENT") || requestedRole.equals("ADVISOR")) {
            if (!matchesChoice(faculty, Set.of(
                    "NAS", "EDUCATION", "HUMANITIES", "AGRICULTURE AND ECONOMICS"))) {
                throw new IllegalArgumentException("Select a valid faculty.");
            }
        } else if (requestedRole.equals("LECTURER")
                && !matchesChoice(faculty, Set.of(
                        "ICT301", "COMPUTER SCIENCE", "DATA SCIENCE",
                        "BED", "AGRICULTURE", "BCOM ACCOUNTING"))) {
            throw new IllegalArgumentException("Select a valid lecturer faculty.");
        }

        String course = trimToEmpty(request.getCourse());
        if (requestedRole.equals("STUDENT")
                && !matchesChoice(course, Set.of(
                        "ICT301", "COMPUTER SCIENCE", "DATA SCIENCE",
                        "BED", "AGRICULTURE", "BCOM ACCOUNTING"))) {
            throw new IllegalArgumentException("Select a valid course.");
        }

        Module selectedModule = null;
        if (requestedRole.equals("STUDENT")) {
            if (request.getModuleId() == null && moduleRepository.count() > 0) {
                throw new IllegalArgumentException("Select a module.");
            }
            if (request.getModuleId() != null) {
                selectedModule = moduleRepository.findById(request.getModuleId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Select a valid module."));
            }
        }

        if (!requestedRole.equals("STUDENT")
                && !requestedRole.equals("LECTURER")
                && !requestedRole.equals("ADVISOR")
                && !requestedRole.equals("COUNSELOR")) {
            throw new IllegalArgumentException("Please select a valid account type.");
        }

        String profileNumber = requestedRole.equals("STUDENT")
                ? trimToEmpty(request.getStudentNumber())
                : trimToEmpty(request.getStaffNumber());
        if (profileNumber.isBlank()) {
            throw new IllegalArgumentException(
                    requestedRole.equals("STUDENT")
                            ? "Official SPU student number is required."
                            : "Staff number is required.");
        }

        boolean profileNumberExists = switch (requestedRole) {
            case "STUDENT" -> studentRepository.existsByStudentNumber(profileNumber);
            case "LECTURER" -> lecturerRepository.existsByStaffNumber(profileNumber);
            case "ADVISOR" -> advisorRepository.existsByStaffNumber(profileNumber);
            case "COUNSELOR" -> counselorRepository.existsByStaffNumber(profileNumber);
            default -> false;
        };
        if (profileNumberExists) {
            throw new IllegalArgumentException(
                    "This " + (requestedRole.equals("STUDENT") ? "student" : "staff")
                            + " number is already registered.");
        }

        Role role = roleRepository.findByRoleName(requestedRole)
                .orElseThrow(() -> new IllegalStateException(
                        "The " + requestedRole + " role is missing from the database."));

        // Create the user account.
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setActive(false);

        User savedUser = userRepository.save(user);

        String departmentOrSpecialization =
                trimToEmpty(request.getDepartmentOrSpecialization());
        switch (requestedRole) {
            case "STUDENT" -> {
                Student student = new Student();
                student.setUser(savedUser);
                student.setStudentNumber(profileNumber);
                student.setFaculty(faculty.toUpperCase());
                student.setCourse(course);
                studentRepository.save(student);

                if (selectedModule != null) {
                    StudentModule studentModule = new StudentModule();
                    studentModule.setStudent(student);
                    studentModule.setModule(selectedModule);
                    studentModule.setAcademicYear(Year.now().getValue());
                    studentModule.setSemester("1");
                    studentModuleRepository.save(studentModule);
                }
            }
            case "LECTURER" -> {
                Lecturer lecturer = new Lecturer();
                lecturer.setUser(savedUser);
                lecturer.setStaffNumber(profileNumber);
                lecturer.setFaculty(faculty);
                lecturer.setDepartment(faculty);
                lecturerRepository.save(lecturer);
            }
            case "ADVISOR" -> {
                Advisor advisor = new Advisor();
                advisor.setUser(savedUser);
                advisor.setStaffNumber(profileNumber);
                advisor.setFaculty(faculty.toUpperCase());
                advisor.setDepartment(faculty.toUpperCase());
                advisorRepository.save(advisor);
            }
            case "COUNSELOR" -> {
                Counselor counselor = new Counselor();
                counselor.setUser(savedUser);
                counselor.setStaffNumber(profileNumber);
                counselor.setSpecialization(departmentOrSpecialization);
                counselorRepository.save(counselor);
            }
            default -> throw new IllegalArgumentException("Please select a valid account type.");
        }

        return savedUser;
    }

        private String trimToEmpty(String value) {
                return value == null ? "" : value.trim();
        }

        private boolean matchesChoice(String value, Set<String> choices) {
                return choices.stream().anyMatch(choice -> choice.equalsIgnoreCase(value));
        }
}