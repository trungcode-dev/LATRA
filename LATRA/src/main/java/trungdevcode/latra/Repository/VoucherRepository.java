package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findAllByOrderByIdDesc();
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);

    // Hàm tìm Voucher theo Code để quét tại quầy POS
    Optional<Voucher> findByCode(String code);
}