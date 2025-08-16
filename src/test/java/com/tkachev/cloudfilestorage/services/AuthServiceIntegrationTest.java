package com.tkachev.cloudfilestorage.services;


import com.tkachev.cloudfilestorage.BaseIntegrationTest;
import com.tkachev.cloudfilestorage.dto.PersonDTO;
import com.tkachev.cloudfilestorage.exceptions.UserAlreadyExists;
import com.tkachev.cloudfilestorage.models.Person;
import com.tkachev.cloudfilestorage.repositories.PeopleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private PeopleRepository peopleRepository;

    @BeforeEach
    void clearDb() {
        peopleRepository.deleteAll();
    }

    @Test
    void userShouldBeCreated() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("test");
        personDTO.setPassword("test");
        authService.registerUser(personDTO);

        Optional<Person> personOpt = peopleRepository.findByUsername(personDTO.getUsername());
        assertTrue(personOpt.isPresent());
        assertEquals(personDTO.getUsername(), personOpt.get().getUsername());
    }

    @Test
    void userShouldNotBeCreatedIfAlreadyExists() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("test");
        personDTO.setPassword("test");
        authService.registerUser(personDTO);

        try {
            authService.registerUser(personDTO);
        } catch (Exception e) {
            assertTrue(e instanceof UserAlreadyExists);
        }

        Optional<Person> personOpt = peopleRepository.findByUsername(personDTO.getUsername());
        assertTrue(personOpt.isPresent());
        assertEquals(personDTO.getUsername(), personOpt.get().getUsername());
    }

}
