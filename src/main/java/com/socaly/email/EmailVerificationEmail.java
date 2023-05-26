package com.socaly.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationEmail {
    private String subject;
    private String recipient;
    private String emailAddress;
    private String username;
    private String verificationLink;
    private String profileImage;
}