package day.happy365.dropx.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Content {
    private Integer contentId;
    private String extractCode;
    private String contentType;
    private String status;
    private LocalDateTime expireAt;
    private LocalDateTime createAt;
    private String objectName;
}
