package com.mesi.scipower.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

    private String document;
    private String reference;
    private int size;

}
