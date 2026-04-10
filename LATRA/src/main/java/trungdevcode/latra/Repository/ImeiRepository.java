package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.Imei;
import trungdevcode.latra.Entity.ProductVariant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImeiRepository extends JpaRepository<Imei, Long> {

    Optional<Imei> findByImeiCode(String imeiCode);

    long countByVariantAndStatus(ProductVariant variant, String status);

    List<Imei> findByVariantAndStatus(ProductVariant variant, String status);
}