package trungdevcode.latra.Dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    // Thay vì trả ra userId, ông nên trả ra Tên của user để Frontend hiện "Nguyễn Văn A đã bình luận"
    private String userName;
}
