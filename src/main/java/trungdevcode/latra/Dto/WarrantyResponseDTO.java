package trungdevcode.latra.Dto;

import java.time.LocalDate;

public class WarrantyResponseDTO {
    private String productName;
    private String imei;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public WarrantyResponseDTO(String productName, String imei, LocalDate startDate, LocalDate endDate, String status) {
        this.productName = productName;
        this.imei = imei;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
