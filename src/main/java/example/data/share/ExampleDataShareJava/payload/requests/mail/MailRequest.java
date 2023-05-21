package example.data.share.ExampleDataShareJava.payload.requests.mail;

import lombok.Data;

import java.util.Map;

@Data
public class MailRequest {
    private String from;
    private String to;
    private String subject;
    private Map<String, Object> model;
}
