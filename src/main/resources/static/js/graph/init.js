function drawGraph() {
    const graph = new graphology.Graph();

    fetch("/get_nodes", { method: "GET" }).then(async response => {
        let result = await response.json();
        for (let i = 0; i < result.length; i++) {
            graph.addNode(`${result[i].label}`, result[i]);
        }

        fetch("/get_edges", { method: "GET" }).then(async response => {
            let result = await response.json();
            for (let i = 0; i < result.length; i++) {
                graph.addEdge(result[i].document, result[i].reference, { size: 1, color: "purple", type: "arrow" });
            }

            const sigmaInstance = new Sigma(graph, document.getElementById("container"));
        });
    });

}
