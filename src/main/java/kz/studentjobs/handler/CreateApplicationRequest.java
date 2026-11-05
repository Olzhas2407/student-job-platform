package kz.studentjobs.handler;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApplicationRequest(

        @NotBlank(message = "business key must not be blank")
        String businessKey,

        @NotBlank(message = "title must not be blank")
        @Size(max = 120)
        String title
) {
}