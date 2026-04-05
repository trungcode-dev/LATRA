package trungdevcode.latra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LatraApplication {

    public static void main(String[] args) {
        SpringApplication.run(LatraApplication.class, args);
        System.out.println("======================================================");
        System.out.println("🚀 HỆ THỐNG LATRA ĐÃ KHỞI CHẠY THÀNH CÔNG!");
        System.out.println("🌍 Truy cập API tại: http://localhost:8080/api/admin/users");
        System.out.println("======================================================");
    }

}
