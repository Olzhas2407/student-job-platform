package kz.studentjobs.domain;

import java.util.List;

public class ApplicationPolicy implements ApplicationRule {

    private final List<ApplicationRule> rules;

    public ApplicationPolicy(List<ApplicationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public void check(ApplicationStatus from, ApplicationStatus to) {
        for (ApplicationRule rule : rules) {
            rule.check(from, to);
        }
    }

    public ApplicationStatus move(
            ApplicationStatus from,
            ApplicationStatus to
    ) {
        check(from, to);
        return to;
    }
}