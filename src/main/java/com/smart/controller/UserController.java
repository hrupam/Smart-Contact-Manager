package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		User user = this.userRepository.getUserByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("title", "SCM | Dashboard");
		return "normal/dashboard";
	}
}
