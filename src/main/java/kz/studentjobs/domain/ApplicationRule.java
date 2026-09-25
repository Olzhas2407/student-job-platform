package kz.studentjobs.domain;

public interface ApplicationRule {

    void check(ApplicationStatus from, ApplicationStatus to);
}