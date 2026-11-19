package kz.studentjobs.security;

import kz.studentjobs.config.Application;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityEndpointsTest {

    @Autowired
    private MockMvc mvc;


    @Test
    void healthIsOpenWithoutToken()
            throws Exception {

        mvc.perform(
                        get("/actuator/health")
                )
                .andExpect(
                        status().isOk()
                );
    }


    @Test
    void meReturnsAuthenticatedUsername()
            throws Exception {

        mvc.perform(
                        get("/api/me")
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        "manager"
                                                                )
                                                )
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_MANAGER"
                                                        )
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.username")
                                .value("manager")
                )
                .andExpect(
                        jsonPath("$.authorities[0]")
                                .value("ROLE_MANAGER")
                );
    }
}