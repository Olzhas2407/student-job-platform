package kz.studentjobs;

public class ApplicationPolicy {

    public ApplicationStatus move(
            ApplicationStatus from,
            ApplicationStatus to
    ) {

        if (from == ApplicationStatus.DRAFT
                && to == ApplicationStatus.SUBMITTED) {

            return to;
        }

        if (from == ApplicationStatus.SUBMITTED
                && to == ApplicationStatus.APPROVED) {

            return to;
        }

        throw new IllegalStateException(
                "Transition from " + from + " to " + to + " is forbidden"
        );
    }
}