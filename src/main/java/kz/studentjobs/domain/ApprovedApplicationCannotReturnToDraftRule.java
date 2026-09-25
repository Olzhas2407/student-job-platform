package kz.studentjobs.domain;

public class ApprovedApplicationCannotReturnToDraftRule
        implements ApplicationRule {

    @Override
    public void check(ApplicationStatus from, ApplicationStatus to) {

        if (from == ApplicationStatus.APPROVED
                && to == ApplicationStatus.DRAFT) {

            throw new IllegalStateException(
                    "Approved application cannot return to draft"
            );
        }
    }
}