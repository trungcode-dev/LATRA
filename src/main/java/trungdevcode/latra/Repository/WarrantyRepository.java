package trungdevcode.latra.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.Dto.WarrantyResponseDTO;
import trungdevcode.latra.Entity.Warranty; // Import đúng tên file vừa tạo

import java.util.List;

public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    @Query("SELECT new trungdevcode.latra.Dto.WarrantyResponseDTO(p.name, w.imei, w.startDate, w.endDate, w.status) " +
            "FROM Warranty w " +
            "JOIN w.order o " +
            "JOIN o.user u " +
            "JOIN w.variant v " +
            "JOIN v.product p " +
            "WHERE u.phone = :phone")
    List<WarrantyResponseDTO> findWarrantyDetailsByPhone(@Param("phone") String phone);
}