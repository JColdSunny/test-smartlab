package com.jcs.interview.dto;

import java.time.LocalDateTime;

public record ErrorResponse(String massage, LocalDateTime timestamp) {
}
