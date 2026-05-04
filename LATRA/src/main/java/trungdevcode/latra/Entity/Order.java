package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    // ==========================================
    // THÊM 3 CỘT ĐỂ XỬ LÝ THANH TOÁN KẾT HỢP
    // ==========================================
    private String paymentMethod; // "CASH" (Tiền mặt), "TRANSFER" (Chuyển khoản), "SPLIT" (Kết hợp)
    private BigDecimal cashAmount;     // Số tiền mặt khách đưa
    private BigDecimal transferAmount; // Số tiền khách chuyển khoản
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipments shipment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
}