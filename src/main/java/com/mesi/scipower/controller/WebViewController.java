package com.mesi.scipower.controller;

import com.mesi.scipower.service.SwitchControllerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class WebViewController {

    private final SwitchControllerService controllerService;
    private final ApplicationContext applicationContext;

    @Autowired
    public WebViewController(SwitchControllerService controllerService,
                             ApplicationContext applicationContext) {
        this.controllerService = controllerService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public WebView webView;

    @FXML
    public TextField parseURL;

    @FXML
    protected void refresh() {
        webView.getEngine().load(parseURL.getText());
        parseURL.setText(webView.getEngine().getLocation());
    }

    @FXML
    protected void parse() {
        if (!parseURL.getText().contains("elibrary")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Ошибка");
            alert.setContentText("Я пока не умею обрабатывать страницы данного типа");
            alert.showAndWait();

            return;
        }

        String elibHTML = (String) webView.getEngine().executeScript("document.documentElement.outerHTML");
        Document elibDoc = Jsoup.parse(elibHTML);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/keyword.txt"))) {
            String pageNum;
            try {
                Elements links = elibDoc.select("table[bgcolor='#ffffff']")
                        .get(2).select("a[href*='query_results.asp?pagenum=']");
                pageNum = links.get(links.size() - 1).attr("href").split("\\?")[1];
                pageNum = pageNum.split("=")[1];
            } catch (IndexOutOfBoundsException exception) {
                log.error("There is only one page");
                writer.write("There is only one page\n");
                pageNum = "1";
            }

            log.info("Count of pages: " + pageNum);
            writer.write("Count of pages: " + pageNum + "\n");

            Elements papers = elibDoc.select("table[bgcolor='#ffffff']")
                    .get(1).select("tr[bgcolor='#f5f5f5']");
            int paperCount = papers.size();

            log.info("Count of papers on one page: " + paperCount);
            writer.write("Count of papers on one page: " + paperCount + "\n");
            papers.forEach(paper -> {
                Elements paperInfo = paper.getElementsByTag("td");

                String paperId = paperInfo.get(0).text();
                String paperLink = paperInfo.get(1).getElementsByTag("a").get(0).attr("href");
                String paperTitle = paperInfo.get(1).getElementsByTag("span").text();

                log.info(paperId + " " + paperLink + " " + paperTitle);
                try {
                    writer.write(paperId + " " + paperLink + " " + paperTitle + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            writer.flush();

        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().getWindow().hide();
        controllerService.switchController(
                "load-data", applicationContext
        );
    }

}
