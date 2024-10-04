package com.mesi.scipower.service.graph;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
public class NodeService {

    private final List<ParseDocument> dataList;
    private final Set<Reference> referenceList;

    private final Set<Node> nodeList;

    @Autowired
    @SuppressWarnings("unchecked")
    public NodeService(ApplicationContext context) {
        this.dataList = (CopyOnWriteArrayList<ParseDocument>) context.getBean("dataList");
        this.referenceList = (CopyOnWriteArraySet<Reference>) context.getBean("referenceList");

        this.nodeList = (CopyOnWriteArraySet<Node>) context.getBean("nodeList");
    }

    public boolean setNodes() {
        long startTime = System.currentTimeMillis();

        referenceList.parallelStream().forEach(reference -> {
            var document = new Node(reference.getDocument().getTitle());
            var referenceDocument = new Node(reference.getReference().getTitle());

            nodeList.add(document);
            nodeList.add(referenceDocument);
        });

        this.calculateNodesSize();

        long stopTime = System.currentTimeMillis();

        log.info("Node process: " + (stopTime - startTime) + " ms");
        log.info("Nodes: " + nodeList.size());

        return nodeList.isEmpty();
    }

    public void calculateNodesSize() {
        nodeList.parallelStream().forEach(node -> {
            int x = (int) (Math.random() * dataList.size());
            int y = (int) (Math.random() * dataList.size()) / 2;

            node.setX(x);
            node.setY(y);

            int nodeRefCount = 1;
            for (var ref : referenceList) {
                if (ref.getReference().getTitle().equals(node.getLabel())) nodeRefCount++;
            }
            node.setSize(nodeRefCount);
        });
    }

}
