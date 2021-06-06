package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		model.addAttribute("firstname", firstName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("title", "SCM | Dashboard");
		return "normal/dashboard";
	}

	@RequestMapping("/profile")
	public String viewProfile(Model m, Principal p) {

		User user = this.userRepository.getUserByUsername(p.getName());
		m.addAttribute("user", user);
		m.addAttribute("contactsCount", user.getContacts().size());
		m.addAttribute("title", "SCM | User Profile");

		return "normal/profile";
	}

//	Showing Contact Form
	@GetMapping("/addcontact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "SCM | Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/contact_form";
	}

//	ADD CONTACT HANDLER
	@PostMapping("/process-addcontact")
	public String addContactHandler(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
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
			session.setAttribute("message", new Message(contact.getName() + " added successfully!", "alert-success"));
			return "normal/contact_form";

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "normal/contact_form";
		}
	}

	@GetMapping("/contacts/{page}")
	public String getContacts(@PathVariable("page") int page, Model model, Principal p, HttpSession session) {
		model.addAttribute("title", "SCM | Contacts");

		User user = this.userRepository.getUserByUsername(p.getName());

		/* THIS CAN ALSO BE USED */
//		List<Contact> contacts = this.contactRepository.getContactsByUser(user);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.getContactsByUserId(user.getId(), pageable);

		if (page >= contacts.getTotalPages())
			session.setAttribute("message", new Message("No more contacts available", "alert-warning"));

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/contacts";
	}

	@GetMapping("/contact/{cid}")
	public String getContactDetails(@PathVariable("cid") int cid, Model model, Principal p, HttpSession session) {

		try {

			User user = this.userRepository.getUserByUsername(p.getName());

			Contact contact = this.contactRepository.getContactByUserIdAndCid(user.getId(), cid);

			model.addAttribute("title", "SCM | Contact");

			if (contact == null)
				throw new Exception("Contact doesnot exist!");

			model.addAttribute("contact", contact);

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-warning"));
		}
		return "normal/contact_details";
	}

	@SuppressWarnings("finally")
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, Principal p, HttpSession session) {

		try {
			Contact contact = this.contactRepository.findById(cid).get();
			User user = this.userRepository.getUserByUsername(p.getName());
			if (contact.getUser().getId() != user.getId())
				throw new Exception("You are trying to delete a contact which doesnot exist!");

			this.contactRepository.delete(contact);

			session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));

		} finally {
			return "redirect:/user/contacts/0";
		}
	}

	@GetMapping("/update/{cid}")
	public String updateContactForm(@PathVariable("cid") int cid, Principal p, HttpSession session, Model m) {

		try {

			User user = this.userRepository.getUserByUsername(p.getName());
			Contact contact = this.contactRepository.findById(cid).get();
			if (contact.getUser().getId() != user.getId())
				throw new Exception("You are trying to update a contact which doesnot exist!");

			m.addAttribute("title", "SCM | Update Contact");
			m.addAttribute("contact", contact);
			return "normal/updatecontact_form";

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "redirect:/user/contacts/0";
		}

	}

	@PostMapping("/process-updatecontact")
	public String updateContactHandler(@Valid @ModelAttribute("contact") Contact updatedContact,
			BindingResult bindingResult, @RequestParam("contactId") String contactId, Principal p,
			HttpSession session) {

		try {

			if (bindingResult.hasErrors())
				return "normal/updatecontact_form";

			User user = this.userRepository.getUserByUsername(p.getName());
			Contact contact = this.contactRepository.findById(Integer.parseInt(contactId)).get();
			if (contact.getUser().getId() != user.getId())
				throw new Exception("Don't try to be oversmart!");

			contact.setName(updatedContact.getName());
			contact.setNickname(updatedContact.getNickname());
			contact.setEmail(updatedContact.getEmail());
			contact.setPhone(updatedContact.getPhone());
			contact.setWork(updatedContact.getWork());
			contact.setDescription(updatedContact.getDescription());

			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact has been updated successfully", "alert-success"));
			return "redirect:/user/contacts/0";
		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "redirect:/user/contacts/0";
		}
	}
}
