package trungdevcode.latra.Dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String username; // Mã NV
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Long roleId;     // 1: ADMIN, 2: STAFF
    private String roleName; // Trả về tên hiển thị
    private Integer status;
}