package example.data.share.ExampleDataShareJava.payload.responses.file;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponse {
    @NotBlank
    private String id;

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileDownloadUri;
}
