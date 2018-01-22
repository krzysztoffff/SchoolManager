package pl.schoolmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.schoolmanager.entity.School;
import pl.schoolmanager.entity.Student;
import pl.schoolmanager.entity.User;
import pl.schoolmanager.entity.UserRole;
import pl.schoolmanager.repository.SchoolRepository;
import pl.schoolmanager.repository.StudentRepository;
import pl.schoolmanager.repository.UserRepository;
import pl.schoolmanager.repository.UserRoleRepository;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	@Autowired
	private SchoolRepository schoolRepo;

	// CREATE
	@GetMapping("/create")
	public String createStudent(Model m) {
		m.addAttribute("student", new Student());
		return "student/new_student";
	}

	@PostMapping("/create")
	public String createStudentPost(@Valid @ModelAttribute Student student, BindingResult bindingResult, Model m) {
		if (bindingResult.hasErrors()) {
			return "student/new_student";
		}
		this.studentRepository.save(student);
		return "index";
	}

	// Make new student automatically creating new user role
	@GetMapping("/user_student")
	public String newStudentFromUser(Model m) {
		m.addAttribute("student", new Student());
		return "student/user_student";
	}

	@PostMapping("/user_student")
	public String newStudentFromUserPost(@Valid @ModelAttribute Student student, BindingResult bindingResult, Model m) {
		if (bindingResult.hasErrors()) {
			return "student/user_student";
		}
		User user = getLoggedUser();
		UserRole userRole = new UserRole();
		userRole.setUsername(user.getUsername());
		userRole.setUserRole("ROLE_STUDENT");
		userRole.setSchool(student.getSchool());
		userRole.setUser(user);
		student.setUserRole(userRole);
		this.studentRepository.save(student);
		return "redirect:/student/all";
	}

	// READ
	@GetMapping("/view/{studentId}")
	public String viewStudent(Model m, @PathVariable long studentId) {
		Student student = this.studentRepository.findOne(studentId);
		m.addAttribute("student", student);
		return "student/show_student";
	}

	// UPDATE
	@GetMapping("/update/{studentId}")
	public String updateStudent(Model m, @PathVariable long studentId) {
		Student student = this.studentRepository.findOne(studentId);
		m.addAttribute("student", student);
		return "student/edit_student";
	}

	@PostMapping("/update/{studentId}")
	public String updateStudentPost(@Valid @ModelAttribute Student student, BindingResult bindingResult,
			@PathVariable long studentId) {
		if (bindingResult.hasErrors()) {
			return "student/edit_student";
		}
		student.setId(studentId);
		this.studentRepository.save(student);
		return "index";
	}

	// DELETE
	@GetMapping("/delete/{studentId}")
	public String deleteStudent(@PathVariable long studentId) {
		this.studentRepository.delete(studentId);
		return "index";
	}

	// SHOW ALL
	@ModelAttribute("availableStudents")
	public List<Student> getStudents() {
		return this.studentRepository.findAll();
	}

	@GetMapping("/all")
	public String all(Model m) {
		return "student/all_students";
	}

	// Additional methods
	private User getLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
		return this.userRepo.findOneByUsername(username);
	}

	@ModelAttribute("userRolesForSelect")
	public List<String> userRolesForSelect() {
		List<String> userRolesForSelect = UserRole.getRolesForSelect();
		return userRolesForSelect;
	}

	@ModelAttribute("availableSchools")
	public List<School> availableSchools() {
		List<School> availableSchools = this.schoolRepo.findAll();
		return availableSchools;
	}

}
