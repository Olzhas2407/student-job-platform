package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationId;
import kz.studentjobs.domain.ApplicationNotificationPort;
import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.persistence.ApplicationJdbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRule rules;

    @Mock
    private ApplicationJdbc applications;

    @Mock
    private ApplicationNotificationPort notifications;

    @InjectMocks
    private ApplicationService service;

    @Test
    void registerUsesOutboundPort() {

        ApplicationId id = ApplicationId.newId();

        service.register(
                id,
                "APP-2001",
                "Student application"
        );

        verify(applications).insert(
                id.asUuid(),
                "APP-2001",
                "DRAFT",
                "Student application"
        );

        verify(notifications)
                .applicationRegistered("APP-2001");
    }
}