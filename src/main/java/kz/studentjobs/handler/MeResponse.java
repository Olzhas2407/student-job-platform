package kz.studentjobs.handler;

import java.util.List;

public record MeResponse(
        String username,
        List<String> authorities
) {
}