package trungdevcode.latra.Dto;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String username; // Thêm trường này để nhận từ Frontend
    private String name;
    private String phone;
    private String email;
    private String address;
    private Double totalSpent;
    private Integer status;
}