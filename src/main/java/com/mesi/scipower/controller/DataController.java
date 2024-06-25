package com.mesi.scipower.controller;

import com.mesi.scipower.dto.DataTableDTO;
import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
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

    private List<ParseDocument> getDTList(List<ParseDocument> dataList, int start, int length) {
        List<ParseDocument> resultList;

        if ((start + length) > dataList.size()) resultList = dataList.subList(start, dataList.size());
        else resultList = dataList.subList(start, start + length);

        return resultList;
    }

    private void writeToFile(List<String> refList, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String ref : refList) writer.write(ref + "\n");
            writer.flush();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    @GetMapping("data")
    public String dataPage(Model model) {
        model.addAttribute("thead", Arrays.stream(ParseDocument.class.getDeclaredFields()).map(Field::getName).toList());
        model.addAttribute("data", dataList);

        return "data-page";
    }

    @GetMapping("get-data")
    public ResponseEntity<List<ParseDocument>> getUploadedData() {
        return ResponseEntity.ok(dataList);
    }

    @PostMapping("upload-data")
    public ResponseEntity<HttpStatus> uploadData(@RequestBody MultipartFile[] files) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        String[] fileParams; String fileExtension;

        List<CompletableFuture<List<ParseDocument>>> futureList = new ArrayList<>();
        for (MultipartFile file : files) {
            fileParams = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            fileExtension = fileParams[fileParams.length - 1];

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

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        for (var future : futureList) dataList.addAll(future.get());

        long endTime = System.currentTimeMillis();
        log.info("Process time: " + (endTime - startTime) + " ms");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("clear")
    public @ResponseBody boolean clearDataList() {
        dataList.clear();
        return true;
    }

    @GetMapping("get-ref")
    public ResponseEntity<HttpStatus> getRef() {
        List<String> refList = dataList.stream().map(ParseDocument::getReferences).toList();
        writeToFile(refList, "/Users/nikol/Desktop/ref.txt");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("get-kw")
    public ResponseEntity<HttpStatus> getKW() {
        List<String> kwList = dataList.stream().map(ParseDocument::getAuthorKeywords).toList();
        writeToFile(kwList, "/Users/nikol/Desktop/kw.txt");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("get_datatable")
    public ResponseEntity<DataTableDTO> test(@RequestParam Map<String, String> params) {
        List<ParseDocument> resultList;
        DataTableDTO resultDto = new DataTableDTO();

        int draw = Integer.parseInt(params.get("draw"));
        int start = Integer.parseInt(params.get("start"));
        int length = Integer.parseInt(params.get("length"));
        String search = params.get("search[value]");

        if (!search.isEmpty()) {
            List<ParseDocument> filteredList = dataList.parallelStream().filter(
                    doc -> doc.toString().substring(14, doc.toString().length() - 1).contains(search)).toList();
            resultList = getDTList(filteredList, start, length);
            resultDto.setRecordsFiltered(filteredList.size());
        }
        else {
            resultList = getDTList(dataList, start, length);
            resultDto.setRecordsFiltered(dataList.size());
        }

        resultDto.setData(resultList.toArray(new ParseDocument[0]));
        resultDto.setRecordsTotal(dataList.size());
        resultDto.setDraw(draw);

        return ResponseEntity.ok(resultDto);
    }

}
