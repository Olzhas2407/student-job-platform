package kz.studentjobs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Profile("dev")
public class DevUsersConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder
    ) {

        UserDetails recruiter = User.builder()
                .username("recruiter")
                .password(
                        passwordEncoder.encode("student-dev")
                )
                .roles("RECRUITER")
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(
                        passwordEncoder.encode("student-dev")
                )
                .roles("MANAGER")
                .build();

        return new InMemoryUserDetailsManager(
                recruiter,
                manager
        );
    }
}