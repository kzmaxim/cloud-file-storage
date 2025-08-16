package com.tkachev.cloudfilestorage.services;

import com.tkachev.cloudfilestorage.dto.PersonDTO;
import com.tkachev.cloudfilestorage.exceptions.UserAlreadyExists;
import com.tkachev.cloudfilestorage.exceptions.UserNotFoundException;
import com.tkachev.cloudfilestorage.models.Person;
import com.tkachev.cloudfilestorage.repositories.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(PersonDTO personDTO) {
        Optional<Person> existingPerson = peopleRepository.findByUsername(personDTO.getUsername());
        if (existingPerson.isPresent()) {
            throw new UserAlreadyExists();
        }
        Person person = fromDTO(personDTO);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleRepository.save(person);
    }

    private Person fromDTO(PersonDTO personDTO) {
        Person person = new Person();
        person.setUsername(personDTO.getUsername());
        person.setPassword(personDTO.getPassword());
        person.setRole("ROLE_USER");
        return person;
    }
}
