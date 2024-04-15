package com.mesi.scipower.controller;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import com.mesi.scipower.service.impl.SwitchControllerServiceImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class DataController {

    private final SwitchControllerServiceImpl controllerService;
    private final ApplicationContext applicationContext;
    private final ParserService parserService;
    private final FileChooser fileChooser;

    @Autowired
    public DataController(SwitchControllerServiceImpl controllerService, @Qualifier("CSV") ParserService parserService,
                          ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.controllerService = controllerService;
        this.parserService = parserService;

        fileChooser = new FileChooser();

        log.info("Application Context ID: " + this.applicationContext.getId());
        log.info("load-data is loaded");
    }

    private final ObservableList<ParseDocument> documentData = FXCollections.observableArrayList();

    @FXML
    private TableView<ParseDocument> documentTable;

    @FXML
    private Label fileName;

    @FXML
    protected void initialize() {
        Field[] fields = ParseDocument.class.getDeclaredFields();

        for (Field field : fields) {
            TableColumn<ParseDocument, String> column = new TableColumn<>();

            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            column.setText(field.getName());

            documentTable.getColumns().add(column);
        }

        documentTable.setOnMouseClicked(mouseEvent -> {
            SelectionModel<ParseDocument> selectionModel = documentTable.getSelectionModel();
            log.info(selectionModel.getSelectedItem().toString());
        });
    }

    public void getData() {
        @SuppressWarnings("unchecked")
        var parseDocs = (List<ParseDocument>) applicationContext.getBean("parsedDocuments");

        log.info(String.valueOf(parseDocs.size()));

        documentData.addAll(parseDocs);
        documentTable.setItems(documentData);
    }

    @FXML
    protected void loadData(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);
        fileName.setText(String.format("Выбрано %d файлов", selectedFile.size()));

        CompletableFuture<Void> parserProcess = new CompletableFuture<>();
        for (File file : selectedFile) {
            parserProcess = CompletableFuture.runAsync(() -> parserService.parseFile(file.getAbsolutePath()));
        }
        parserProcess.whenComplete((a, b) ->
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Готово");
                alert.setContentText("Загруженные файлы обработаны");
                alert.showAndWait();

                getData();
            })
        );
    }

    @FXML
    protected void goToWebView(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().getWindow().hide();
        controllerService.switchController(
                "webview-controller", applicationContext
        );
    }

    @FXML
    protected void refresh() {
        getData();
    }

}
