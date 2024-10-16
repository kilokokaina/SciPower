const graph = new graphology.Graph();

async function getNodes() {
    const response = await fetch("api/node/get", { method: "GET" });
    const result = await response.json();

    if (response.status === 200) {
        console.log("Nodes: OK");
    }

    for (let i = 0; i < result.length; i++) {
        graph.addNode(`${result[i].label}`, result[i]);
    }
}

async function getEdges() {
    const response = await fetch("api/edge/get", { method: "GET" });
    const result = await response.json();

    if (response.status === 200) {
        console.log("Edges: OK");
    }

    for (let i = 0; i < result.length; i++) {
        try {
            graph.addEdge(result[i].document.label, result[i].reference.label, {size: 1, color: "#ffbd73", type: "arrow"});
        } catch (error) {
            console.log('Doc: ' + result[i].document.label + ' \nRef: ' + result[i].reference.label);
            console.log(error.message);
        }
    }
}

async function drawGraph() {
    await getNodes();
    await getEdges()

    const sigmaInstance = new Sigma(graph, document.getElementById("container"));
}
