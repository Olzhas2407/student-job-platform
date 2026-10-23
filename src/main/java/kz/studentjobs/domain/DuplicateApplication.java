package kz.studentjobs.domain;

public class DuplicateApplication extends RuntimeException {

    public DuplicateApplication(String businessKey) {
        super("duplicate application: " + businessKey);
    }
}