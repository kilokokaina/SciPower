package com.mesi.scipower.service.impl;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Qualifier("CSV")
public class CSVParserService implements ParserService {

    @Async
    @Override
    public CompletableFuture<List<ParseDocument>> parseFile(MultipartFile file) {
        log.info(String.format("CSVParser process %s file...", file.getOriginalFilename()));

        String[] headerArray = {
                "Authors","Author full names","Author(s) ID","Title","Year",
                "Source title","Volume","Issue","Art. No.","Page start","Page end","Page count",
                "Cited by","DOI","Link","Affiliations","Authors with affiliations","Abstract",
                "Author Keywords","Index Keywords","Molecular Sequence Numbers","Chemicals/CAS",
                "Tradenames","Manufacturers","Funding Details","Funding Texts","References",
                "Correspondence Address","Editors","Publisher","Sponsors","Conference name",
                "Conference date","Conference location","Conference code","ISSN","ISBN","CODEN",
                "PubMed ID","Language of Original Document","Abbreviated Source Title",
                "Document Type","Publication Stage","Open Access","Source","EID"
        };

        List<ParseDocument> parseDocumentList = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.Builder.create(CSVFormat.RFC4180).setHeader(headerArray).build();

        try(CSVParser parser = new CSVParser(new InputStreamReader(file.getInputStream()), csvFormat)) {
            List<CSVRecord> csvData = parser.getRecords();
            Field[] fields; String[] values;

            csvData.remove(0);
            for (CSVRecord csv: csvData) {
                ParseDocument document = new ParseDocument();

                fields = document.getClass().getDeclaredFields();
                values = csv.values();

                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    try {
                        fields[i].set(document, values[i]);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                parseDocumentList.add(document);
            }

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        log.info("Process completed: " + file.getOriginalFilename() + "; Rows: " + parseDocumentList.size());

        return CompletableFuture.completedFuture(parseDocumentList);
    }

}
