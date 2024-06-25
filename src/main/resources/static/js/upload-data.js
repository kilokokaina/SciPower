// let dataTable;
let parseWindow;

async function uploadData() {
    const files = document.querySelector('#papers-list').files;
    const filesList = document.querySelector('#files-list');
    // const uploadButton = document.querySelector('#upload-button');
    const formData = new FormData();

    console.log(files);
    console.log(files.length);
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    let startFetchTime = Date.now();

    fetch('upload-data', {
        method: 'POST',
        body: formData
    }).then(async response => {
        // let result = await response.json();
        // console.log(result);

        filesList.innerHTML = '';
        for (let i = 0; i < files.length; i++) {
            if (response.ok) {
                filesList.innerHTML += `<h5 id="file-${i}"><span class="badge badge-outline-success">Обработано</span> ${files[i].name}</h5>`
            } else {
                filesList.innerHTML += `<h5 id="file-${i}"><span class="badge badge-outline-danger">Ошибка загрузки</span> ${files[i].name}</h5>`
            }
        }

        let endFetchTime = Date.now();
        console.log('Fetch: ' + (endFetchTime - startFetchTime) + ' ms')

        renderTable();
    });

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
        ajax: 'get_datatable',
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
    console.log('Render: ' + (endRenderTime - startRenderTime) + ' ms')
}

function siteParser() {
    const windowFeatures = "width=1024,height=720,popup";
    parseWindow = window.open("https://elibrary.ru", "mozillaWindow", windowFeatures);
}

function extractData() {
    fetch('https://elibrary.ru', { mode: 'no-cors' })
        .then(async response => console.log(await response.headers));
}

