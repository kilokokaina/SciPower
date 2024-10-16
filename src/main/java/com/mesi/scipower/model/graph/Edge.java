package com.mesi.scipower.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

    private Node document;
    private Node reference;
    private int weight;
    private int size;

}
