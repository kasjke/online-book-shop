package org.teamchallenge.bookshop.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateEmailResponse {
    private String newEmail;
    private String accessToken;
    private String refreshToken;
}