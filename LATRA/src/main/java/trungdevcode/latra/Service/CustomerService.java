package trungdevcode.latra.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.Dto.CustomerDTO;
import trungdevcode.latra.Entity.User;
import trungdevcode.latra.Repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;

    public List<CustomerDTO> getAllCustomers() {
        List<User> users = userRepository.findAllCustomersOnly();
        List<CustomerDTO> dtos = new ArrayList<>();
        for (User u : users) {
            // Lọc bỏ status = 2 (Đã xóa)
            if (u.getStatus() != null && u.getStatus() == 2) continue;
            CustomerDTO dto = new CustomerDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setName(u.getFullName());
            dto.setPhone(u.getPhone());
            dto.setEmail(u.getEmail());
            dto.setAddress(u.getAddress());
            dto.setStatus(u.getStatus() != null ? u.getStatus() : 1);
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public void addCustomer(CustomerDTO dto) {
        validateCustomerData(dto);
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword("123456");
        user.setFullName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPhone(dto.getPhone().trim());
        user.setAddress(dto.getAddress());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        User savedUser = userRepository.save(user);
        userRepository.assignCustomerRole(savedUser.getId());
    }

    @Transactional
    public void updateCustomer(Long id, CustomerDTO dto) {
        validateCustomerData(dto);
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy!"));
        user.setUsername(dto.getUsername().trim());
        user.setFullName(dto.getName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        userRepository.save(user);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy!"));
        user.setStatus(2); // Soft Delete
        userRepository.save(user);
    }

    private void validateCustomerData(CustomerDTO dto) {
        if (dto.getUsername() == null || !dto.getUsername().trim().matches("^[a-zA-Z0-9_]{4,20}$"))
            throw new RuntimeException("Username 4-20 ký tự, không dấu cách!");
        if (dto.getName() == null || !dto.getName().trim().matches("^[\\p{L}\\s]+$"))
            throw new RuntimeException("Họ tên không chứa số/ký tự đặc biệt!");
        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^(0[3|5|7|8|9])[0-9]{8}$"))
            throw new RuntimeException("SĐT VN 10 số không hợp lệ!");
        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new RuntimeException("Email không hợp lệ!");
    }
}