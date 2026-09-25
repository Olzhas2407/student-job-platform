package kz.studentjobs.domain;

public class ApplicationTransitionRule implements ApplicationRule {

    @Override
    public void check(ApplicationStatus from, ApplicationStatus to) {

        if (from == ApplicationStatus.DRAFT
                && to == ApplicationStatus.SUBMITTED) {
            return;
        }

        if (from == ApplicationStatus.SUBMITTED
                && to == ApplicationStatus.APPROVED) {
            return;
        }

        if (from == ApplicationStatus.APPROVED
                && to == ApplicationStatus.DRAFT) {
            return;
        }

        throw new IllegalStateException(
                "Transition from " + from + " to " + to + " is forbidden"
        );
    }
}