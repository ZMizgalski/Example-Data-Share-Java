package example.data.share.ExampleDataShareJava.model.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Files")
public class File {
    @Id
    private String id;

    @NotNull
    private String username;

    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;

    @NotBlank
    private byte[] file;
}
