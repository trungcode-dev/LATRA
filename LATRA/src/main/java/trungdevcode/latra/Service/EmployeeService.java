package trungdevcode.latra.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.EmployeeDTO;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final UserRepository userRepository;

    public List<EmployeeDTO> getAllEmployees() {
        List<User> users = userRepository.findAllEmployeesOnly();

        return users.stream().map(u -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setFullName(u.getFullName());
            dto.setPhone(u.getPhone());
            dto.setEmail(u.getEmail());
            dto.setAddress(u.getAddress());
            dto.setStatus(u.getStatus() != null ? u.getStatus() : 1);

            Long roleId = userRepository.findRoleIdByUserId(u.getId());
            dto.setRoleId(roleId);

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void addEmployee(EmployeeDTO dto) {
        validateEmployeeData(dto, false);

        if(userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Mã nhân viên (Username) đã tồn tại!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setFullName(dto.getFullName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAddress(dto.getAddress());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setPassword("123456");

        User savedUser = userRepository.save(user);

        if (dto.getRoleId() != null) {
            userRepository.assignRoleToUser(savedUser.getId(), dto.getRoleId());
        }
    }

    @Transactional
    public void updateEmployee(Long id, EmployeeDTO dto) {
        // NGHIỆP VỤ: Bảo vệ Super Admin
        if (id == 1L) {
            throw new RuntimeException("Lỗi bảo mật: Bất khả xâm phạm! Không thể sửa thông tin của Super Admin.");
        }

        validateEmployeeData(dto, true);

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("Email này đã bị người khác sử dụng!");
        }
        if (userRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new RuntimeException("Số điện thoại này đã bị người khác sử dụng!");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        user.setFullName(dto.getFullName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAddress(dto.getAddress());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());

        userRepository.save(user);

        if (dto.getRoleId() != null) {
            userRepository.updateUserRole(id, dto.getRoleId());
        }
    }

    @Transactional
    public void deleteEmployee(Long id) {
        // NGHIỆP VỤ: Bảo vệ Super Admin
        if (id == 1L) {
            throw new RuntimeException("Lỗi bảo mật: Bất khả xâm phạm! Không thể xóa tài khoản Super Admin.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        // Soft Delete: Chuyển trạng thái về 2 (Đã xóa) để ẩn khỏi UI nhưng giữ DB
        user.setStatus(2);
        userRepository.save(user);
    }

    private void validateEmployeeData(EmployeeDTO dto, boolean isUpdate) {
        if (!isUpdate) {
            if (dto.getUsername() == null || !dto.getUsername().trim().matches("^[a-zA-Z0-9_]{3,20}$"))
                throw new RuntimeException("Mã NV phải từ 3-20 ký tự, không dấu cách!");
        }
        if (dto.getFullName() == null || !dto.getFullName().trim().matches("^[\\p{L}\\s]+$"))
            throw new RuntimeException("Họ tên không được chứa số hoặc ký tự đặc biệt!");
        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^(0[3|5|7|8|9])[0-9]{8}$"))
            throw new RuntimeException("SĐT VN 10 số không hợp lệ!");
        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new RuntimeException("Email không hợp lệ!");
    }
}