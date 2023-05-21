package example.data.share.ExampleDataShareJava.payload.requests.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 50, message = "Username has to be at least 3 characters long")
    private String username;

    @NotBlank
    @Size(min = 3, max = 100, message = "Email has to be at least 3 characters long")
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank
    @Size(min = 8, message = "Password has to be at least 8 characters long")
    private String password;
}
