package com.mesi.scipower.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    private String label;
    private int x;
    private int y;
    private int size;
    private int weight;

    public Node(String label) {
        this.label = label;
    }

}
