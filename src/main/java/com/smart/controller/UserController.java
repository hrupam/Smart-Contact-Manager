package com.smart.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@ModelAttribute
	public void userData(Model model, Principal principal) {
		User user = this.userRepository.getUserByUsername(principal.getName());
		String firstName = user.getName().split(" ")[0];
		user.setName(firstName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model) {

		model.addAttribute("title", "SCM | Dashboard");
		return "normal/dashboard";
	}

	@GetMapping("/addcontact")
	public String contact(Model model) {
		model.addAttribute("title", "SCM | Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/contact_form";
	}

	@PostMapping("/process-addcontact")
	public String addContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			return "normal/contact_form";
		}
		System.out.println(contact);
		return "normal/contact_form";
	}

}
