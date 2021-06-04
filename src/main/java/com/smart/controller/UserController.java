package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void userData(Model model, Principal principal) {
		User user = this.userRepository.getUserByUsername(principal.getName());
		String firstName = user.getName().split(" ")[0];
		user.setName(firstName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("title", "SCM | Dashboard");
		return "normal/dashboard";
	}

//	Showing Contact Form Handler
	@GetMapping("/addcontact")
	public String contact(Model model) {
		model.addAttribute("title", "SCM | Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/contact_form";
	}

//	ADD CONTACT HANDLER
	@PostMapping("/process-addcontact")
	public String addContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {

		try {

			if (bindingResult.hasErrors())
				return "normal/contact_form";

			if (!file.isEmpty()) {

//				adding datetime to image file name
				String imgData[] = file.getOriginalFilename().split("\\.");
				if (imgData.length != 2)
					throw new Exception("Invalid image file. File must be in [filename].[extension] format.");

				String imgFname = imgData[0] + "_" + LocalDateTime.now().toString();
				String imgExt = imgData[1];

				String imageName = imgFname + "." + imgExt;

				contact.setImage(imageName);

				File fileReference = new ClassPathResource("static/images/contactPhotos").getFile();
				Path path = Paths.get(fileReference.getAbsolutePath() + File.separator + imageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			contact.setUser(this.userRepository.getUserByUsername(principal.getName()));
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact added successfully!", "alert-success"));
			return "normal/contact_form";

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "normal/contact_form";
		}
	}

	@GetMapping("/contacts")
	public String getContacts(Model model, Principal p) {
		model.addAttribute("title", "SCM | Contacts");

		User user = this.userRepository.getUserByUsername(p.getName());

		/* THIS CAN ALSO BE USED */
//		List<Contact> contacts = this.contactRepository.getContactsByUser(user);

		List<Contact> contacts = this.contactRepository.getContactsByUserId(user.getId());

		/* FOR SHOWING THE ID SEQUENCIALLY */
		class IdCounter {
			private int k = 0;

			public int getIncrement() {
				return ++this.k;
			}
		}

		model.addAttribute("contacts", contacts);
		model.addAttribute("counter", new IdCounter());

		return "normal/contacts";
	}

}
