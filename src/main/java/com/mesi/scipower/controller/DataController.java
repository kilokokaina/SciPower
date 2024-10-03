package com.mesi.scipower.controller;

import com.mesi.scipower.model.ParseDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class DataController {

    private final String[] HEADERS;
    private final List<ParseDocument> dataList;

    @Autowired
    @SuppressWarnings("unchecked")
    public DataController(ApplicationContext context) {
        this.HEADERS = ((List<String>) context.getBean("HEADERS")).toArray(new String[0]);
        this.dataList = (List<ParseDocument>) context.getBean("dataList");
    }

    @GetMapping("data")
    public String dataPage(Model model) {
        model.addAttribute("thead", HEADERS);
        model.addAttribute("data", dataList);

        return "data-page";
    }

}
