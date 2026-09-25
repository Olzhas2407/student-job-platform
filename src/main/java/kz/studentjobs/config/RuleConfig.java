package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationPolicy;
import kz.studentjobs.domain.ApplicationRule;
import kz.studentjobs.domain.ApplicationTransitionRule;
import kz.studentjobs.domain.ApprovedApplicationCannotReturnToDraftRule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RuleConfig {

    @Bean
    public ApplicationRule applicationRule() {

        return new ApplicationPolicy(
                List.of(
                        new ApprovedApplicationCannotReturnToDraftRule(),
                        new ApplicationTransitionRule()
                )
        );
    }
}