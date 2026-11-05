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

import java.util.UUID;

@Service
public class ApplicationService {

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

    public int count(String businessKey) {
        return applications.count(businessKey);
    }
}