package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "SCM | Home");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "SCM | About");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SCM | Signup");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping("/signin")
	public String signin(Model model) {
		model.addAttribute("title", "SCM | Signin");
		return "signin";
	}

	@PostMapping("/user_registration")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingresult,
			@RequestParam(name = "agreement", defaultValue = "false") boolean agreement,
			@RequestParam(name = "confirm_password", defaultValue = "") String confirmPassword, HttpSession session) {

		try {
			if (!agreement) {
				throw new Exception("You must agree with T&C");
			}
			if (bindingresult.hasErrors()) {
				return "signup";
			}
			if (confirmPassword.isBlank()) {
				throw new Exception("Please confirm your password");
			} else if (!(user.getPassword()).equals(confirmPassword))
				throw new Exception("Password didn't match");

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			this.userRepository.save(user);

			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			return "signup";

		} catch (DataIntegrityViolationException e) {
			session.setAttribute("message", new Message("Email address already exists", "alert-danger"));
			return "signup";
		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
}
