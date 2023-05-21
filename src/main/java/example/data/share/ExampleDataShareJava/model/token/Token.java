package example.data.share.ExampleDataShareJava.model.token;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Data
@Document(collection = "Tokens")
public class Token {
    private String email;

    private boolean isUsed;

    private Date expiryDate;

    private String token;

    public void setExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
