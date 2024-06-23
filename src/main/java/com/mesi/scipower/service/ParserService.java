package com.mesi.scipower.service;

import com.mesi.scipower.model.ParseDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ParserService {

    CompletableFuture<List<ParseDocument>> parseFile(MultipartFile file);

}
