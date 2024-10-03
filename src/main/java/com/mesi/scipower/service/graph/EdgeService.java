package com.mesi.scipower.service.graph;

import com.mesi.scipower.model.Reference;
import com.mesi.scipower.model.graph.Edge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class EdgeService {

    private final Set<Reference> referenceList;
    private final Set<Edge> edgeList;

    @Autowired
    @SuppressWarnings("unchecked")
    public EdgeService(ApplicationContext context) {
        this.referenceList = (Set<Reference>) context.getBean("referenceList");
        this.edgeList = (Set<Edge>) context.getBean("edgeList");
    }

    public boolean calculateEdges() {
        referenceList.parallelStream().forEach(reference -> {
            String documentTitle = reference.getDocument().getTitle();
            String referenceTitle = reference.getReference().getTitle();

            edgeList.add(new Edge(documentTitle, referenceTitle, 1));
        });

        log.info("Edges: " + edgeList.size());

        return !edgeList.isEmpty();
    }

}
