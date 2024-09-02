package com.example.uploadservice.authentication.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
public class AuthenticateResponse {

    private String token;
}
