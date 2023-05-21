package example.data.share.ExampleDataShareJava.model.content;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "Content")
public class Content {
    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String content;
}
