package example.data.share.ExampleDataShareJava.payload.responses.teacherResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherResponse {
    @NotBlank
    private String id;

    @NotBlank
    private String name;
}
