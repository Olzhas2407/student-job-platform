package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.domain.ApplicationStatus;

import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRule rules;

    public ApplicationService(ApplicationRule rules) {
        this.rules = rules;
    }

    public ApplicationStatus move(
            ApplicationStatus from,
            ApplicationStatus to
    ) {
        rules.check(from, to);
        return to;
    }
}