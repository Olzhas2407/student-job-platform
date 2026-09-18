package kz.studentjobs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationPolicyTest {

    private final ApplicationPolicy policy = new ApplicationPolicy();

    @ParameterizedTest
    @CsvSource({
            "DRAFT, SUBMITTED, true",
            "SUBMITTED, APPROVED, true",
            "DRAFT, APPROVED, false",
            "APPROVED, DRAFT, false"
    })
    void testStatusTransitions(
            ApplicationStatus from,
            ApplicationStatus to,
            boolean allowed
    ) {

        if (allowed) {

            ApplicationStatus result = policy.move(from, to);

            assertEquals(to, result);

        } else {

            assertThrows(
                    IllegalStateException.class,
                    () -> policy.move(from, to)
            );
        }
    }

    @Test
    void nullIdMustThrow() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new ApplicationId(null)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void blankIdMustThrow(String value) {

        assertThrows(
                IllegalArgumentException.class,
                () -> new ApplicationId(value)
        );
    }
}