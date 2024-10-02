package com.mesi.scipower.service.impl;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Edge;
import com.mesi.scipower.model.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DataListService {

    private final List<ParseDocument> dataList;
    private final Set<Reference> referenceList;

    private final Set<Edge> edgeList;
    private final Set<Node> nodeList;

    @Autowired
    @SuppressWarnings("unchecked")
    public DataListService(ApplicationContext context) {
        this.dataList = (List<ParseDocument>) context.getBean("dataList");
        this.referenceList = (Set<Reference>) context.getBean("referenceList");

        this.nodeList = (Set<Node>) context.getBean("nodeList");
        this.edgeList = (Set<Edge>) context.getBean("edgeList");
    }

    private ParseDocument findByTitle(String title) {
        return dataList.parallelStream().filter(document -> document.getTitle().equals(title)).toList().get(0);
    }

    public boolean getReference() {
        List<String> documentTitles = dataList.stream().map(ParseDocument::getTitle).toList();
        long startTime = System.currentTimeMillis();

        dataList.parallelStream().forEach(document -> {
            for (String reference : document.getReferences().split("; ")) {
                Matcher pageMatcher = Pattern.compile(", pp. \\d+-\\d+").matcher(reference);
                reference = pageMatcher.replaceAll("");

                Matcher yearMatcher = Pattern.compile(", \\(\\d{4}\\)").matcher(reference);
                reference = yearMatcher.replaceAll("");

                Matcher nameMatcher = Pattern.compile("\\D+ (\\D{1,2}\\.)+, ").matcher(reference);
                reference = nameMatcher.replaceAll("");

                Matcher numbersMatcher = Pattern.compile(", \\d+").matcher(reference);
                reference = numbersMatcher.replaceAll("");

                String[] refComponent = reference.split(", ");

                if (documentTitles.contains(refComponent[0])) {
                    referenceList.add(new Reference(document, this.findByTitle(refComponent[0])));
                    edgeList.add(new Edge(document.getTitle(), refComponent[0], 1));
                }
            }
        });

        long stopTime = System.currentTimeMillis();
        log.info("Reference process: " + (stopTime - startTime) + " ms");

        log.info("References: " + referenceList.size());
        log.info("Edges: " + edgeList.size());

        return referenceList.isEmpty();
    }

    public Set<String> getKeyWordList() {
        List<String> documentKeyWords = dataList.stream().map(ParseDocument::getAuthorKeywords).toList();
        Set<String> keyWordsSet = Collections.synchronizedSet(new HashSet<>());

        long startTime = System.currentTimeMillis();

        documentKeyWords.parallelStream().forEach(keyWordString -> {
            String[] keyWords = keyWordString.split("; ");
            for (String keyWord : keyWords) {
                if (keyWord.length() > 1) keyWordsSet.add(keyWord.toLowerCase());
            }
        });

        long stopTime = System.currentTimeMillis();
        log.info("KW process: " + (stopTime - startTime) + " ms");

        return keyWordsSet;
    }

    public boolean updateNodes() {
        long startTime = System.currentTimeMillis();

        referenceList.parallelStream().forEach(reference -> {
            Node document = new Node();
            document.setLabel(reference.getDocument().getTitle());

            Node referenceDocument = new Node();
            referenceDocument.setLabel(reference.getReference().getTitle());

            nodeList.add(document);
            nodeList.add(referenceDocument);
        });

        nodeList.parallelStream().forEach(node -> {
            int x = (int) (Math.random() * dataList.size());
            int y = (int) (Math.random() * dataList.size());

            node.setX(x);
            node.setY(y);

            int nodeRefCount = 1;
            for (Reference ref : referenceList) {
                if (ref.getReference().getTitle().equals(node.getLabel())) {
                    log.info(node.getLabel() + " " + nodeRefCount++);
                }
            }
            node.setSize(nodeRefCount);
        });

        long stopTime = System.currentTimeMillis();
        log.info("Node process: " + (stopTime - startTime) + " ms");
        log.info("Nodes: " + nodeList.size());

        return nodeList.isEmpty();
    }

}
