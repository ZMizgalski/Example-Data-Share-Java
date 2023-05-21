package example.data.share.ExampleDataShareJava.payload.requests.forgotPassword;

import example.data.share.ExampleDataShareJava.model.validators.FieldMatch;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords must be the same!")
public class ForgotPasswordRequestModel {
    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private String token;

    public String getToken() { return token;}
}
