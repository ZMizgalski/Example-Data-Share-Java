package example.data.share.ExampleDataShareJava.payload.requests.content;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentRequest {
    @NotNull
    private String id;

    @NotNull
    private String content;
}
