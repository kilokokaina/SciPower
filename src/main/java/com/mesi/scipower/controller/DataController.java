package com.mesi.scipower.controller;

import com.mesi.scipower.model.ParseDocument;
import com.mesi.scipower.service.ParserService;
import com.mesi.scipower.service.impl.SwitchControllerServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Component
public class DataController {

    private final SwitchControllerServiceImpl controllerService;
    private final ApplicationContext applicationContext;
    private final ParserService parserService;
    private final FileChooser fileChooser;
    private final TaskExecutor executor;

    @Autowired
    public DataController(SwitchControllerServiceImpl controllerService,
                          @Qualifier("taskExecutor") TaskExecutor executor,
                          @Qualifier("CSV") ParserService parserService,
                          ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.controllerService = controllerService;
        this.parserService = parserService;
        this.executor = executor;

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

    private void getData() {
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

        for (File file : selectedFile) {
            executor.execute(() -> parserService.parseFile(file.getAbsolutePath()));
        }
    }

    @FXML
    protected void refresh() {
        getData();
    }

}
