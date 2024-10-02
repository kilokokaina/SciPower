package com.mesi.scipower.controller;

import com.mesi.scipower.dto.DataTableDTO;
import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Edge;
import com.mesi.scipower.model.graph.Node;
import com.mesi.scipower.service.ParserService;
import com.mesi.scipower.service.impl.DataListService;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class DataController {

    private final String[] HEADERS;

    private final List<ParseDocument> dataList;
    private final Set<Reference> referenceList;

    private final Set<Node> nodeList;
    private final Set<Edge> edgeList;

    private final ParserService csvParserService;
    private final ParserService risParserService;

    private final DataListService dataListService;

    @Autowired
    @SuppressWarnings("unchecked")
    public DataController(ApplicationContext context, @Qualifier("CSV") ParserService csvParserService,
                          @Qualifier("RIS") ParserService risParserService, DataListService dataListService) {
        this.HEADERS = ((List<String>) context.getBean("HEADERS")).toArray(new String[0]);

        this.dataList = (List<ParseDocument>) context.getBean("dataList");
        this.referenceList = (Set<Reference>) context.getBean("referenceList");

        this.nodeList = (Set<Node>) context.getBean("nodeList");
        this.edgeList = (Set<Edge>) context.getBean("edgeList");

        this.csvParserService = csvParserService;
        this.risParserService = risParserService;

        this.dataListService = dataListService;
    }

    private static List<ParseDocument> getDTList(List<ParseDocument> dataList, int start, int length) {
        return dataList.subList(start, Math.min((start + length), dataList.size()));
    }

    @GetMapping("data")
    public String dataPage(Model model) {
        model.addAttribute("thead", HEADERS);
        model.addAttribute("data", dataList);

        return "data-page";
    }

    @GetMapping("get_data")
    public @ResponseBody ResponseEntity<List<ParseDocument>> getUploadedData() {
        return ResponseEntity.ok(dataList);
    }

    @PostMapping("upload_data")
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

    @GetMapping("get_datatable")
    public @ResponseBody ResponseEntity<DataTableDTO> test(@RequestParam Map<String, String> params) {
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

    @GetMapping("get_nodes")
    public @ResponseBody ResponseEntity<Set<Node>> getNodes() {
        return ResponseEntity.ok(nodeList);
    }

    @GetMapping("update_nodes")
    public @ResponseBody ResponseEntity<HttpStatus> updateNodes() {
        if (!dataListService.updateNodes()) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("get_edges")
    public @ResponseBody ResponseEntity<Set<Edge>> getEdges() {
        return ResponseEntity.ok(edgeList);
    }

    @GetMapping("update_edges")
    public @ResponseBody ResponseEntity<HttpStatus> updateEdges() {
        if (!dataListService.getReference()) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("get_ref")
    public @ResponseBody ResponseEntity<Set<Reference>> getRef() {
        return ResponseEntity.ok(referenceList);
    }

    @GetMapping("get_kw")
    public @ResponseBody ResponseEntity<Set<String>> getKW() {
        Set<String> result = dataListService.getKeyWordList();
        log.info("Key words: " + result.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("clear")
    public @ResponseBody boolean clearDataList() {
        dataList.clear();
        referenceList.clear();
        nodeList.clear();
        edgeList.clear();

        return true;
    }

}
