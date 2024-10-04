package com.mesi.scipower.api;

import com.mesi.scipower.dto.DataTableDTO;
import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.service.ParserService;
import com.mesi.scipower.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("api/data")
public class DataAPI {

    private final List<ParseDocument> dataList;
    private final Set<Reference> referenceList;

    private final ParserService csvParserService;
    private final ParserService risParserService;

    private final DataService dataService;

    @Autowired
    @SuppressWarnings("unchecked")
    public DataAPI(ApplicationContext context, @Qualifier("CSV") ParserService csvParserService,
                          @Qualifier("RIS") ParserService risParserService, DataService dataService) {
        this.dataList = (CopyOnWriteArrayList<ParseDocument>) context.getBean("dataList");
        this.referenceList = (CopyOnWriteArraySet<Reference>) context.getBean("referenceList");

        this.csvParserService = csvParserService;
        this.risParserService = risParserService;

        this.dataService = dataService;
    }

    private static List<ParseDocument> getDTList(List<ParseDocument> dataList, int start, int length) {
        return dataList.subList(start, Math.min((start + length), dataList.size()));
    }

    @GetMapping("get")
    public @ResponseBody ResponseEntity<List<ParseDocument>> getUploadedData() {
        return ResponseEntity.ok(dataList);
    }

    @PostMapping("upload")
    public @ResponseBody ResponseEntity<HttpStatus> uploadDataAsync(@RequestBody MultipartFile file) throws ExecutionException, InterruptedException {
        String[] fileParams;String fileExtension;
        List<ParseDocument> parserFuture;

        fileParams = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        fileExtension = fileParams[fileParams.length - 1];

        switch (fileExtension) {
            case "csv" -> {
                parserFuture = csvParserService.parseFile(file).get();
                dataList.addAll(parserFuture);
            }
            case "ris" -> {
                parserFuture = risParserService.parseFile(file).get();
                dataList.addAll(parserFuture);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("get/datatable")
    public @ResponseBody ResponseEntity<DataTableDTO> test(@RequestParam Map<String, String> params) {
        List<ParseDocument> resultList;
        var resultDto = new DataTableDTO();

        int draw = Integer.parseInt(params.get("draw"));
        int start = Integer.parseInt(params.get("start"));
        int length = Integer.parseInt(params.get("length"));
        String search = params.get("search[value]");

        if (!search.isEmpty()) {
            var filteredList = dataList.parallelStream().filter(
                    doc -> doc.toString().substring(14, doc.toString().length() - 1).contains(search)).toList();
            resultList = getDTList(filteredList, start, length);
            resultDto.setRecordsFiltered(filteredList.size());
        } else {
            resultList = getDTList(dataList, start, length);
            resultDto.setRecordsFiltered(dataList.size());
        }

        resultDto.setData(resultList.toArray(new ParseDocument[0]));
        resultDto.setRecordsTotal(dataList.size());
        resultDto.setDraw(draw);

        return ResponseEntity.ok(resultDto);
    }

    @GetMapping("get/ref")
    public @ResponseBody ResponseEntity<Set<Reference>> getRef() {
        return ResponseEntity.ok(referenceList);
    }

    @GetMapping("update/ref")
    public @ResponseBody ResponseEntity<HttpStatus> updateEdges() {
        if (!dataService.findReferences()) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("get/kw")
    public @ResponseBody ResponseEntity<Set<String>> getKW() {
        var result = dataService.getKeyWordList();
        log.info("Key words: " + result.size());

        return ResponseEntity.ok(result);
    }

}
