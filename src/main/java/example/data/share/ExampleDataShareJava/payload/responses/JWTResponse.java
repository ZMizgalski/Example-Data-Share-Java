package example.data.share.ExampleDataShareJava.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTResponse {
    private final String type = "Bearer";
    private String token;
}
