package trungdevcode.latra.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color", columnDefinition = "NVARCHAR(50)")
    private String color;

    @Column(name = "storage", columnDefinition = "NVARCHAR(50)")
    private String storage;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "sku", unique = true)
    private String sku;
    @Column(name = "condition", length = 50)
    private String condition;
    @ManyToOne
    @JoinColumn(name = "product_id")
    // THÊM DÒNG NÀY ĐỂ CHẶN VÒNG LẶP:
    @JsonIgnoreProperties("variants")
    private Product product;

    // Trong file ProductVariant.java
    @OneToMany(mappedBy = "variant")
    @JsonIgnore // <--- THÊM VÀO ĐÂY NỮA
    private List<Imei> imeis;
}
