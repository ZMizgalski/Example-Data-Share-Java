package example.data.share.ExampleDataShareJava.payload.responses.file;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileModel {
    @NotBlank
    private String id;

    @NotBlank
    private String fileName;
}
