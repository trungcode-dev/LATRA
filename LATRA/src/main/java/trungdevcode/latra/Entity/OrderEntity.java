package trungdevcode.latra.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    /* =========================================
       CÁC CLASS ENTITY LỒNG NHAU (GỘP CHUNG)
    ========================================= */

    @Entity
    @Table(name = "users")
    @Data
    public static class User {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String fullName;
        private String phone;
        private String email;
    }

    @Entity
    @Table(name = "products")
    @Data
    public static class Product {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
    }

    @Entity
    @Table(name = "product_variants")
    @Data
    public static class ProductVariant {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;

        private String color;
        private String storage;
        private Integer stock;
    }

    @Entity
    @Table(name = "shipments")
    @Data
    public static class Shipment {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "order_id")
        private OrderEntity order;

        private String address;
    }

    @Embeddable
    @Data
    @EqualsAndHashCode
    public static class OrderDetailKey implements Serializable {
        @Column(name = "order_id")
        private Long orderId;

        @Column(name = "variant_id")
        private Long variantId;
    }

    @Entity
    @Table(name = "order_details")
    @Data
    public static class OrderDetail {
        @EmbeddedId
        private OrderDetailKey id;

        @ManyToOne
        @MapsId("orderId")
        @JoinColumn(name = "order_id")
        private OrderEntity order;

        @ManyToOne
        @MapsId("variantId")
        @JoinColumn(name = "variant_id")
        private ProductVariant variant;

        private Integer quantity;
        private BigDecimal price;
    }
}