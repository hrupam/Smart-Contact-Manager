package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	/* BOTH QUERY EXECUTES FINE */

//	@Query("select c from Contact c where c.user= :user")
//	List<Contact> getContactsByUser(@Param("user") User user);

	@Query("select c from Contact c where c.user.id= :userId")
	Page<Contact> getContactsByUserId(@Param("userId") int userId, Pageable pageable);

	@Query("select c from Contact c where c.user.id= :userId and c.cid= :cid")
	Contact getContactByUserIdAndCid(@Param("userId") int userId, @Param("cid") int cid);

}
