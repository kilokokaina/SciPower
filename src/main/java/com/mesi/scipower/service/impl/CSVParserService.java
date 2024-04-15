package com.mesi.scipower.service.impl;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Service
@Qualifier("CSV")
public class CSVParserService implements ParserService {

    private final ApplicationContext applicationContext;

    @Autowired
    public CSVParserService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Async
    @Override
    public void parseFile(String fileName) {
        String infoString = String.format("CSVParser process %s file...", fileName);
        log.info(infoString);

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

        CSVFormat csvFormat = CSVFormat.Builder
                .create(CSVFormat.RFC4180).setHeader(headerArray).build();

        var parseDocumentList = (List<ParseDocument>) applicationContext.getBean("parsedDocuments");

        try(CSVParser parser = new CSVParser(
                new FileReader(fileName),
                csvFormat)) {

            List<CSVRecord> csvData = parser.getRecords();
            csvData.forEach(csv -> {
                ParseDocument document = new ParseDocument();

                Field[] fields = document.getClass().getDeclaredFields();
                String[] values = csv.values();

                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    try { fields[i].set(document, values[i]); }
                    catch (IllegalAccessException e) { throw new RuntimeException(e); }
                }

                parseDocumentList.add(document);
            });

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        Thread showAlert = new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Готов");
                alert.setContentText("Данные загружены");
                alert.showAndWait();

                return null;
            }
        });
        showAlert.setDaemon(true);
        showAlert.start();

        log.info("Process completed: " + fileName);
    }

}
