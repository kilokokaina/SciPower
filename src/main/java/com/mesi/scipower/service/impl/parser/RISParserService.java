package com.mesi.scipower.service.impl.parser;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Qualifier("RIS")
public class RISParserService implements ParserService {

    @Async
    @Override
    public CompletableFuture<List<ParseDocument>> parseFile(MultipartFile file) {
        var parseDocumentList = new ArrayList<ParseDocument>();
        log.info(String.format("RISParser process %s file...", file.getOriginalFilename()));

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            var document = new ParseDocument();

            String line;
            while((line = reader.readLine()) != null) {
                if (line.contains("AB  - ")) {
                    document.setAbstracts(line.split("AB  - ")[1]);
                }
                if (line.contains("AD  - ")) {
                    String corrAddress = Objects.nonNull(document.getCorrespondenceAddress()) ? document.getCorrespondenceAddress() : "";
                    document.setCorrespondenceAddress(corrAddress + line.split("AD  - ")[1] + "; ");
                }
                if (line.contains("AU  - ")) {
                    String docAuthors = Objects.nonNull(document.getAuthors()) ? document.getAuthors() : "";
                    document.setAuthors(docAuthors + line.split("AU  - ")[1] + "; ");
                }
                if (line.contains("DB  - ")) {
                    document.setSource(line.split("DB  - ")[1]);
                }
                if (line.contains("DO  - ")) {
                    document.setDOI(line.split("DO  - ")[1]);
                }
                if (line.contains("SP  - ")) {
                    document.setPageStart(line.split("SP  - ")[1]);
                }
                if (line.contains("EP  - ")) {
                    document.setPageEnd(line.split("EP  - ")[1]);
                }
                if (line.contains("IS  - ")) {
                    document.setIssue(line.split("IS  - ")[1]);
                }
                if (line.contains("KW  - ")) {
                    String corrAddress = Objects.nonNull(document.getAuthorKeywords()) ? document.getAuthorKeywords() : "";
                    document.setAuthorKeywords(corrAddress + line.split("KW  - ")[1] + "; ");
                }
                if (line.contains("LA  - ")) {
                    document.setLanguageOfOriginalDocument(line.split("LA  - ")[1]);
                }
                if (line.contains("M3  - ")) {
                    document.setDocumentType(line.split("M3  - ")[1]);
                }
                if (line.contains("PB  - ")) {
                    document.setPublisher(line.split("PB  - ")[1]);
                }
                if (line.contains("PY  - ")) {
                    document.setYear(line.split("PY  - ")[1]);
                }
                if (line.contains("SN  - ")) {
                    document.setISSN(line.split("SN  - ")[1]);
                }
                if (line.contains("ST  - ")) {
                    document.setAbbreviatedSourceTitle(line.split("ST  - ")[1]);
                }
                if (line.contains("TI  - ")) {
                    document.setTitle(line.split("TI  - ")[1]);
                }
                if (line.contains("UR  - ")) {
                    document.setLink(line.split("UR  - ")[1]);
                }
                if (line.contains("VL  - ")) {
                    document.setVolume(line.split("VL  - ")[1]);
                }
                if (line.contains("ER  -")) {
                    parseDocumentList.add(document);
                    document = new ParseDocument();
                }
            }
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        log.info("Process completed: " + file.getOriginalFilename());

        return CompletableFuture.completedFuture(parseDocumentList);
    }

}
