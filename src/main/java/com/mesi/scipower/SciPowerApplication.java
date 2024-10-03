package com.mesi.scipower;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Edge;
import com.mesi.scipower.model.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
@EnableAsync
@SpringBootApplication
public class SciPowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SciPowerApplication.class, args);
    }

    @Bean
    @ApplicationScope
    public List<ParseDocument> dataList() {
        return Collections.synchronizedList(new ArrayList<>());
    }

    @Bean
    @ApplicationScope
    public Set<Edge> edgeList() {
        return Collections.synchronizedSet(new HashSet<>());
    }

    @Bean
    @ApplicationScope
    public Set<Node> nodeList() {
        return Collections.synchronizedSet(new HashSet<>());
    }

    @Bean
    @ApplicationScope
    public Set<Reference> referenceList() {
        return Collections.synchronizedSet(new HashSet<>());
    }

    @Bean
    @ApplicationScope
    public List<String> HEADERS() {
        var result = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/model"))) {
            String line;
            while((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return result;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("parser-");
        executor.initialize();

        return executor;
    }

}
