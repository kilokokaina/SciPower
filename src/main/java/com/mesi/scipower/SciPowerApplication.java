package com.mesi.scipower;

import com.mesi.scipower.model.ParseDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@EnableAsync
@SpringBootApplication
public class SciPowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SciPowerApplication.class, args);
    }

    @Bean
    @SessionScope
    public List<ParseDocument> dataList() {
        return new ArrayList<>();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("parser-");
        executor.initialize();

        return executor;
    }

}
