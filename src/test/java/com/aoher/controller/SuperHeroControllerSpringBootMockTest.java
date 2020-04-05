package com.aoher.controller;

import com.aoher.exceptions.NonExistingHeroException;
import com.aoher.model.SuperHero;
import com.aoher.repository.SuperHeroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * This class demonstrates how to test a controller using Spring Boot Test
 * with a MOCK web environment, which makes it similar to just using @WebMvcTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuperHeroControllerSpringBootMockTest {

    private static final String URL = "/superheroes/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuperHeroRepository superHeroRepository;

    private static SuperHero superHero;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<SuperHero> jsonSuperHero;

    @BeforeClass
    public static void setUp() {
        superHero = new SuperHero("Rob", "Mannon", "RobotMan");
    }

    @Before
    public void init() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        int id = 2;

        given(superHeroRepository.getSuperHero(id)).willReturn(superHero);

        MockHttpServletResponse response = mockMvc.perform(
                get(URL + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonSuperHero.write(superHero).getJson(), response.getContentAsString());
    }

    @Test
    public void canRetrieveByIdWhenDoesNotExist() throws Exception {
        int id = 2;

        given(superHeroRepository.getSuperHero(id)).willThrow(new NonExistingHeroException());

        MockHttpServletResponse response = mockMvc.perform(
                get(URL + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue(response.getContentAsString().isEmpty());
    }

    @Test
    public void canRetrieveByNameWhenExists() throws Exception {
        given(superHeroRepository.getSuperHero(superHero.getHeroName()))
                .willReturn(Optional.of(superHero));

        MockHttpServletResponse response = mockMvc.perform(
                get(URL + "?name=" + superHero.getHeroName())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonSuperHero.write(superHero).getJson(), response.getContentAsString());
    }

    @Test
    public void canRetrieveByNameWhenDoesNotExist() throws Exception {
        String wrongName = "wrong name";
        // given
        given(superHeroRepository.getSuperHero(wrongName)).willReturn(Optional.empty());

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get(URL + "?name=" + wrongName)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("null", response.getContentAsString());
    }

    @Test
    public void canCreateANewSuperHero() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(
                post(URL).contentType(MediaType.APPLICATION_JSON).content(
                        jsonSuperHero.write(superHero).getJson()
                )).andReturn().getResponse();

        // then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void headerIsPresent() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(
                get(URL + 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertThat(response.getHeaders("X-SUPERHERO-APP")).containsOnly("super-header");
    }
}
