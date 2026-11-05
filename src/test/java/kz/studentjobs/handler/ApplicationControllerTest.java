package kz.studentjobs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.studentjobs.config.Application;
import org.springframework.test.context.ContextConfiguration;
import kz.studentjobs.config.ApplicationDetails;
import kz.studentjobs.config.ApplicationService;
import kz.studentjobs.domain.ApplicationNotFound;
import kz.studentjobs.domain.DuplicateApplication;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
@ContextConfiguration(classes = Application.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationService service;

    @Test
    void createReturns201() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.register(
                any(),
                eq("APP-1042"),
                eq("Warehouse application")
        )).thenReturn(
                new ApplicationDetails(
                        id,
                        "APP-1042",
                        "DRAFT",
                        "Warehouse application"
                )
        );

        CreateApplicationRequest request =
                new CreateApplicationRequest(
                        "APP-1042",
                        "Warehouse application"
                );

        mvc.perform(
                        post("/api/v1/applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/v1/applications/" + id
                        )
                )
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.businessKey").value("APP-1042"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.title").value("Warehouse application"));
    }

    @Test
    void blankTitleReturns400AndDoesNotCallService() throws Exception {

        CreateApplicationRequest request =
                new CreateApplicationRequest(
                        "APP-1042",
                        "   "
                );

        mvc.perform(
                        post("/api/v1/applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("title")
                ));

        verify(service, never())
                .register(any(), any(), any());
    }

    @Test
    void missingApplicationReturns404() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.findById(id))
                .thenThrow(new ApplicationNotFound(id));

        mvc.perform(
                        get("/api/v1/applications/" + id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void duplicateReturns409() throws Exception {

        when(service.register(
                any(),
                eq("APP-1042"),
                eq("Warehouse application")
        )).thenThrow(
                new DuplicateApplication("APP-1042")
        );

        CreateApplicationRequest request =
                new CreateApplicationRequest(
                        "APP-1042",
                        "Warehouse application"
                );

        mvc.perform(
                        post("/api/v1/applications")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE"));
    }

    @Test
    void invalidUuidReturns400() throws Exception {

        mvc.perform(
                        get("/api/v1/applications/APP-1042")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"))
                .andExpect(jsonPath("$.message").value("id must be a UUID"));
    }

    @Test
    void correlationIdIsReturned() throws Exception {

        CreateApplicationRequest request =
                new CreateApplicationRequest(
                        "APP-1042",
                        ""
                );

        mvc.perform(
                        post("/api/v1/applications")
                                .header("X-Correlation-Id", "test-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correlationId").value("test-123"));
    }
}