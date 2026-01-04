package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private Integer id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Integer categoryId;
    private Integer viewCount;
    private LocalDateTime publishTime;
    private Boolean isDeleted;
}