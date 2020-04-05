package com.aoher.controller;

import com.aoher.exceptions.NonExistingHeroException;
import com.aoher.model.SuperHero;
import com.aoher.repository.SuperHeroRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

/**
 * This class demonstrates how to test a controller using Spring Boot Test
 * (what makes it much closer to a Integration Test)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SuperHeroControllerSpringBootTest {

    private static final String URL = "/superheroes/";

    @MockBean
    private SuperHeroRepository superHeroRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private static SuperHero superHero;

    @BeforeClass
    public static void setUp() {
        superHero = new SuperHero("Rob", "Mannon", "RobotMan");
    }

    @Test
    public void canRetrieveByIdWhenExists() {
        int id = 2;

        given(superHeroRepository.getSuperHero(id)).willReturn(superHero);

        ResponseEntity<SuperHero> response = restTemplate.getForEntity(URL + id, SuperHero.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superHero, response.getBody());
    }

    @Test
    public void canRetrieveByIdWhenDoesNotExist() {
        int id = 2;

        given(superHeroRepository.getSuperHero(id)).willThrow(new NonExistingHeroException());

        ResponseEntity<SuperHero> response = restTemplate.getForEntity(URL + id, SuperHero.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void canRetrieveByNameWhenExists() {
        given(superHeroRepository.getSuperHero(superHero.getHeroName())).willReturn(Optional.of(superHero));

        ResponseEntity<SuperHero> response = restTemplate
                .getForEntity(URL + "?name=" + superHero.getHeroName(), SuperHero.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(superHero, response.getBody());
    }

    @Test
    public void canRetrieveByNameWhenDoesNotExist() {
        String wrongName = "wrong name";

        given(superHeroRepository.getSuperHero(wrongName)).willReturn(Optional.empty());

        ResponseEntity<SuperHero> response = restTemplate.getForEntity(URL + "?name=" + wrongName, SuperHero.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void canCreateANewSuperHero() {
        ResponseEntity<SuperHero> response = restTemplate.postForEntity(URL, superHero, SuperHero.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void headerIsPresent() {
        ResponseEntity<SuperHero> response = restTemplate.getForEntity(URL + 2, SuperHero.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getHeaders().get("X-SUPERHERO-APP")).containsOnly("super-header");
    }
}