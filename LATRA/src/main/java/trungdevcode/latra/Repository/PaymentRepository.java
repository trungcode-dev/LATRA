package trungdevcode.latra.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.Entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
