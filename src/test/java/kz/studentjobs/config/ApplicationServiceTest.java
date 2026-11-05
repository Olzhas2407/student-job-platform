package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationId;
import kz.studentjobs.domain.ApplicationNotificationPort;
import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.persistence.ApplicationJpa;

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
    private ApplicationJpa applications;

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

        verify(notifications)
                .applicationRegistered("APP-2001");
    }
}