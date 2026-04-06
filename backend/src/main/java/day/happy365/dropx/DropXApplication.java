package day.happy365.dropx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableScheduling
@MapperScan("day.happy365.dropx.mapper")
public class DropXApplication {
    public static void main(String[] args) {
        SpringApplication.run(DropXApplication.class, args);
    }
}
