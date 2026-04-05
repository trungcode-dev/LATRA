package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.Entity.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}