package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationId;
import kz.studentjobs.domain.ApplicationNotFound;
import kz.studentjobs.domain.ApplicationNotificationPort;
import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.domain.ApplicationStatus;
import kz.studentjobs.domain.DuplicateApplication;
import kz.studentjobs.persistence.ApplicationEntity;
import kz.studentjobs.persistence.ApplicationJpa;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;

@Service
public class ApplicationService {
    private static final Logger log =
            LoggerFactory.getLogger(ApplicationService.class);
    private final ApplicationRule rules;
    private final ApplicationJpa applications;
    private final ApplicationNotificationPort notifications;

    public ApplicationService(
            ApplicationRule rules,
            ApplicationJpa applications,
            ApplicationNotificationPort notifications
    ) {
        this.rules = rules;
        this.applications = applications;
        this.notifications = notifications;
    }

    public ApplicationStatus move(
            ApplicationStatus from,
            ApplicationStatus to
    ) {
        rules.check(from, to);
        return to;
    }

    @Transactional
    public ApplicationDetails register(
            ApplicationId id,
            String businessKey,
            String title
    ) {
        try {
            ApplicationEntity entity =
                    new ApplicationEntity(
                            id.asUuid(),
                            businessKey,
                            ApplicationStatus.DRAFT.name(),
                            title
                    );

            applications.insert(entity);
            applications.flush();

            notifications.applicationRegistered(businessKey);

            return new ApplicationDetails(
                    entity.getId(),
                    entity.getBusinessKey(),
                    entity.getStatus(),
                    entity.getTitle()
            );

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateApplication(businessKey);
        }
    }

    @Transactional(readOnly = true)
    public ApplicationDetails findById(UUID id) {

        ApplicationEntity entity = applications.findById(id)
                .orElseThrow(() -> new ApplicationNotFound(id));

        return new ApplicationDetails(
                entity.getId(),
                entity.getBusinessKey(),
                entity.getStatus(),
                entity.getTitle()
        );
    }

    @Transactional
    public void insertTwice(String businessKey) {
        try {
            applications.insert(
                    new ApplicationEntity(
                            ApplicationId.newId().asUuid(),
                            businessKey,
                            ApplicationStatus.DRAFT.name(),
                            "First application"
                    )
            );

            applications.insert(
                    new ApplicationEntity(
                            ApplicationId.newId().asUuid(),
                            businessKey,
                            ApplicationStatus.DRAFT.name(),
                            "Second application"
                    )
            );

            applications.flush();

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateApplication(businessKey);
        }
    }
    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public ApplicationDetails submit(
            UUID id,
            String actor
    ) {

        ApplicationEntity entity =
                applications.findById(id)
                        .orElseThrow(
                                () -> new ApplicationNotFound(id)
                        );

        ApplicationStatus from =
                ApplicationStatus.valueOf(
                        entity.getStatus()
                );

        ApplicationStatus to =
                ApplicationStatus.SUBMITTED;

        rules.check(from, to);

        entity.changeStatus(
                to.name()
        );

        applications.flush();

        log.info(
                "Application {} submitted by {}",
                id,
                actor
        );

        return new ApplicationDetails(
                entity.getId(),
                entity.getBusinessKey(),
                entity.getStatus(),
                entity.getTitle()
        );
    }
    public int count(String businessKey) {
        return applications.count(businessKey);
    }
}