package com.mesi.scipower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Controller
public class ParserController {

    @GetMapping("parser")
    public String parser() {
        return "parser-page";
    }

    @GetMapping("get-page-content")
    public @ResponseBody String getHTML(@RequestParam(value = "paper_id") String paperId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("https://elibrary.ru/item.asp?id=%s", paperId)))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Cache-Control", "no-cache")
                .header("Cookie", "SCookieGUID=28F1B9E1%2D75D0%2D4057%2D9007%2DD943A168FADB; SUserID=3978197")
                .header("Pragma", "no-cache")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

}
