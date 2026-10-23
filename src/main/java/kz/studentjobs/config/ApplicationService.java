package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationId;
import kz.studentjobs.domain.ApplicationNotificationPort;
import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.domain.ApplicationStatus;
import kz.studentjobs.domain.DuplicateApplication;
import kz.studentjobs.persistence.ApplicationJdbc;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRule rules;
    private final ApplicationJdbc applications;
    private final ApplicationNotificationPort notifications;

    public ApplicationService(
            ApplicationRule rules,
            ApplicationJdbc applications,
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
    public void register(
            ApplicationId id,
            String businessKey,
            String title
    ) {
        try {
            applications.insert(
                    id.asUuid(),
                    businessKey,
                    ApplicationStatus.DRAFT.name(),
                    title
            );

            notifications.applicationRegistered(businessKey);

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateApplication(businessKey);
        }
    }

    @Transactional
    public void insertTwice(String businessKey) {
        try {
            applications.insert(
                    ApplicationId.newId().asUuid(),
                    businessKey,
                    ApplicationStatus.DRAFT.name(),
                    "First application"
            );

            applications.insert(
                    ApplicationId.newId().asUuid(),
                    businessKey,
                    ApplicationStatus.DRAFT.name(),
                    "Second application"
            );

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateApplication(businessKey);
        }
    }

    public int count(String businessKey) {
        return applications.count(businessKey);
    }
}