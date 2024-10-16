package com.mesi.scipower.service.graph;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
        this.referenceList = (Set<Reference>) context.getBean("referenceList");
        this.nodeList = (Set<Node>) context.getBean("nodeList");
    }

    public Node findByTitle(String title) {
        Node result = null;
        for (var node : nodeList) {
            if (node.getLabel().equals(title)) {
                result = node;
            }
        }

        if (result == null) {
            result = new Node(title);
            result.setX(dataList.size());
            result.setY(dataList.size() / 2);
            result.setWeight(1);
            result.setSize(1);
        }

        return result;
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

    private void calculateNodesSize() {
        nodeList.parallelStream().forEach(node -> {
            int nodeWeight = 1;
            for (var rereference : referenceList) {
                if (rereference.getReference().getTitle().equals(node.getLabel())) nodeWeight++;
            }
            node.setWeight(nodeWeight);

            int nodeSize = (int) Math.round((Math.log(nodeWeight) / 10) * 19 + 1);
            node.setSize(nodeSize);

            node.setX(dataList.size() / 2);
            node.setY(dataList.size() / 4);
        });

        this.calculateNodePlacement();
    }

    private int[] getDelta(Node firstNode, Node secondNode) {
        double alpha = Math.random() * (2 * Math.PI);

        int delta = firstNode.getWeight() * secondNode.getWeight();

        int x = (int) (delta * Math.cos(alpha));
        int y = (int) (delta * Math.sin(alpha));

        return new int[] { x, y };
    }

    private void calculateNodePlacement() {
        var nodeSizeSet = nodeList.stream().map(Node::getWeight).collect(Collectors.toSet());
        log.info("Length: " + nodeSizeSet.size());
        log.info("Sizes: " + nodeSizeSet);

        int percentile = nodeSizeSet.toArray(new Integer[0])[(int) (nodeSizeSet.size() * 0.75)];
        log.info("Percentile: " + percentile);

        var maxWeightNode = nodeList.stream().max(Comparator.comparing(Node::getWeight)).orElse(null);
        assert maxWeightNode != null;
        log.info("Node with max weight: " + maxWeightNode);

        for (Node node : nodeList) {
            if (node.getWeight() < maxWeightNode.getWeight()) {
                var delta = getDelta(node, maxWeightNode);

                node.setX(maxWeightNode.getX() + delta[0]);
                node.setY(maxWeightNode.getY() + delta[1]);

//                for (var reference : referenceList) {
//                    if (reference.getReference().getTitle().equals(node.getLabel())) {
//                        var documentNode = this.findByTitle(reference.getDocument().getTitle());
//                        if (documentNode != null && documentNode.getSize() < percentile) {
//                            var innerDelta = getDelta(documentNode, node);
//
//                            documentNode.setX(maxWeightNode.getX() + innerDelta[0]);
//                            documentNode.setY(maxWeightNode.getY() + innerDelta[1]);
//                        }
//                    }
//                }
            }
        }
    }

}
