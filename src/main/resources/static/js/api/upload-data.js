function uploadData() {
    const files = document.querySelector('#papers-list').files;
    const filesList = document.querySelector('#files-list');
    let formData;
    let counter = 0;

    console.log(files);
    console.log(files.length);
    for (let i = 0; i < files.length; i++) {
        formData = new FormData();
        formData.append('file', files[i]);

        fetch('api/data/upload', {
            method: 'POST',
            body: formData
        }).then(async response => {
            let uploadStatus = document.querySelector(`#file-${i}`);
            if (response.status === 200) {
                uploadStatus.innerHTML = `<h5 id="file-${i}"><span class="badge badge-outline-success">Обработано</span> ${files[i].name}</h5>`;
            } else {
                uploadStatus.innerHTML = `<h5 id="file-${i}"><span class="badge badge-outline-danger">Ошибка загрузки</span> ${files[i].name}</h5>`;
            }

            if (++counter === files.length) {
                renderTable();
                await findReferences();
                await updateGraph();
            }
        });
    }

    filesList.innerHTML = '';
    for (let i = 0; i < files.length; i++) {
        filesList.innerHTML += `<h5 id="file-${i}"><span class="spinner-border text-primary me-1" role="status" aria-hidden="true"></span> ${files[i].name}</h5>`
    }
}

function updateList() {
    const files = document.querySelector('#papers-list').files;
    const filesList = document.querySelector('#files-list');

    filesList.innerHTML = '';
    for (let i = 0; i < files.length; i++) {
        filesList.innerHTML += `<h5 id="file-${i}"><span class="badge badge-outline-warning">Готов к загрузке</span> ${files[i].name}</h5>`
    }
}

function renderTable() {
    let startRenderTime = Date.now();

    new DataTable('#data-list', {
        destroy: true,
        language: {
            url: '//cdn.datatables.net/plug-ins/2.0.8/i18n/ru.json'
        },
        ajax: 'api/data/get/datatable',
        processing: true,
        serverSide: true,
        columns: [
            {data: "authors"},
            {data: "authorFullNames"},
            {data: "authorID"},
            {data: "title"},
            {data: "year"},
            {data: "sourceTitle"},
            {data: "volume"},
            {data: "issue"},
            {data: "artNo"},
            {data: "pageStart"},
            {data: "pageEnd"},
            {data: "pageCount"},
            {data: "citedBy"},
            {data: "doi"},
            {data: "link"},
            {data: "affiliations"},
            {data: "authorsWithAffiliations"},
            {data: "abstracts"},
            {data: "authorKeywords"},
            {data: "indexKeywords"},
            {data: "molecularSequenceNumbers"},
            {data: "chemicalsCAS"},
            {data: "tradenames"},
            {data: "manufacturers"},
            {data: "fundingDetails"},
            {data: "fundingTexts"},
            {data: "references"},
            {data: "correspondenceAddress"},
            {data: "editors"},
            {data: "publisher"},
            {data: "sponsors"},
            {data: "conferenceName"},
            {data: "conferenceDate"},
            {data: "conferenceLocation"},
            {data: "conferenceCode"},
            {data: "issn"},
            {data: "isbn"},
            {data: "coden"},
            {data: "pubMedID"},
            {data: "languageOfOriginalDocument"},
            {data: "abbreviatedSourceTitle"},
            {data: "documentType"},
            {data: "publicationStage"},
            {data: "openAccess"},
            {data: "source"},
            {data: "eid"}
        ],
        responsive: true,
        ordering: false
    });

    let endRenderTime = Date.now();
    console.log('Render: ' + (endRenderTime - startRenderTime) + ' ms');
}

async function findReferences() {
    const startTime = Date.now();

    const response = await fetch("api/data/update/ref", { method: "GET" });
    const status = response.status;

    if (status === 200) console.log('References: OK. ' + (Date.now() - startTime) + ' ms');
}


async function updateGraph() {
    await updateNodes();
    await updateEdges();
}

async function updateNodes() {
    const startTime = Date.now();

    const response = await fetch("api/node/update", { method: "GET" });
    const status = response.status;

    if (status === 200) console.log('Nodes: OK. ' + (Date.now() - startTime) + ' ms');
}

async function updateEdges() {
    const startTime = Date.now();

    const response = await fetch("api/edge/update", { method: "GET" });
    const status = response.status;

    if (status === 200) console.log('Edges: OK. ' + (Date.now() - startTime) + ' ms');
}
