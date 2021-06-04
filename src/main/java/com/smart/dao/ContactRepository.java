package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	/* BOTH QUERY EXECUTES FINE */

	@Query("select c from Contact c where c.user.id= :userId")
	List<Contact> getContactsByUserId(@Param("userId") int userId);

	@Query("select c from Contact c where c.user= :user")
	List<Contact> getContactsByUser(@Param("user") User user);

}
