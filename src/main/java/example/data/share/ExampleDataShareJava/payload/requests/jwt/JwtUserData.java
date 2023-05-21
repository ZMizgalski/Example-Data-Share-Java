package example.data.share.ExampleDataShareJava.payload.requests.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtUserData {
    @NotBlank
    private String token;
}
