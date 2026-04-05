package trungdevcode.latra.Dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Integer status;
    private Set<String> roles; // Chỉ trả về tên Role (vd: ADMIN, USER)
}
