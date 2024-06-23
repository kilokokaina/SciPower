package com.mesi.scipower.controller;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class DataController {

    private final List<ParseDocument> dataList;
    private final ParserService csvParserService;
    private final ParserService risParserService;

    @Autowired
    @SuppressWarnings("unchecked")
    public DataController(ApplicationContext context, @Qualifier("CSV") ParserService csvParserService,
                          @Qualifier("RIS") ParserService risParserService) {
        this.dataList = (List<ParseDocument>) context.getBean("dataList");
        this.csvParserService = csvParserService;
        this.risParserService = risParserService;
    }

    @GetMapping("data")
    public String dataPage(Model model) {
        model.addAttribute("thead", Arrays.stream(ParseDocument.class.getDeclaredFields()).map(Field::getName).toList());
        model.addAttribute("data", dataList);

        return "data-page";
    }

    @GetMapping("upload-data")
    public ResponseEntity<List<ParseDocument>> getUploadedData() {
        return ResponseEntity.ok(dataList);
    }

    @PostMapping("upload-data")
    public ResponseEntity<List<ParseDocument>> uploadData(@RequestBody MultipartFile[] files) throws ExecutionException, InterruptedException {
        List<CompletableFuture<List<ParseDocument>>> futureList = new ArrayList<>();
        for (MultipartFile file : files) {
            String[] fileParams = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            String fileExtension = fileParams[fileParams.length - 1];

            switch (fileExtension) {
                case "csv" -> {
                    CompletableFuture<List<ParseDocument>> parserFuture = csvParserService.parseFile(file);
                    futureList.add(parserFuture);
                }
                case "ris" -> {
                    CompletableFuture<List<ParseDocument>> parserFuture = risParserService.parseFile(file);
                    futureList.add(parserFuture);
                }
            }

        }

        CompletableFuture[] futureArray = new CompletableFuture[futureList.size()];
        CompletableFuture.allOf(futureList.toArray(futureArray)).join();

        for (var future : futureList) dataList.addAll(future.get());

        return ResponseEntity.ok(dataList);
    }

    @GetMapping("clear")
    public @ResponseBody boolean clearDataList() {
        dataList.clear();
        return true;
    }

    @GetMapping("get-ref")
    public ResponseEntity<List<String>> getRef() {
        List<String> refList = dataList.stream().map(ParseDocument::getReferences).toList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/ref.txt"))) {
            for (String ref : refList) writer.write(ref + "\n");
            writer.flush();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        return ResponseEntity.ok(refList);
    }

}
