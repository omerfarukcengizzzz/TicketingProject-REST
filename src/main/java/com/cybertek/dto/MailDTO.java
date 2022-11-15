package com.cybertek.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailDTO {

    private String emailTo;
    private String emailFrom;
    private String message;
    private String token;
    private String subject;
    private String url;

}
