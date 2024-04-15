package com.mesi.scipower;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class ParserTest {

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/keyword.txt"))) {
            Document elibDoc = Jsoup.parse(new File("src/main/resources/elib.html"));

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

}
