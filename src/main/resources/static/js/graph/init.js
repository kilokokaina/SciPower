function drawGraph() {
    const graph = new graphology.Graph();

    fetch("api/node/get", { method: "GET" }).then(async response => {
        let result = await response.json();
        for (let i = 0; i < result.length; i++) {
            graph.addNode(`${result[i].label}`, result[i]);
        }

        fetch("api/edge/get", { method: "GET" }).then(async response => {
            let result = await response.json();
            for (let i = 0; i < result.length; i++) {
                graph.addEdge(result[i].document, result[i].reference, { size: 1, color: "purple", type: "arrow" });
            }

            const sigmaInstance = new Sigma(graph, document.getElementById("container"));
        });
    });

}
