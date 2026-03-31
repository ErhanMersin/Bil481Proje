package com.fellow.app.controller;

import com.fellow.app.dao.EventDAO;
import com.fellow.app.model.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HomeController {

    @FXML
    private Label lblDate;

    // Mini-Timer embedded tracker
    @FXML
    private HBox miniTimerBox;
    @FXML
    private Label lblMiniTimer;

    @FXML
    private ListView<Event> listUpcomingEvents;
    @FXML
    private ComboBox<String> cmbSortEvents;

    private EventDAO eventDAO = new EventDAO();
    private ObservableList<Event> eventsList = FXCollections.observableArrayList();
    private final int DEMO_USER_ID = 1;

    @FXML
    public void initialize() {
        setTodayDate();

        // Setup Sorting ComboBox
        cmbSortEvents.getItems().addAll("By Date (First Delivered)", "New Additions (Added First)");

        javafx.util.Callback<ListView<String>, ListCell<String>> cellFactory = lv -> new ListCell<String>() {
            {
                getStyleClass().add("combo-list-cell");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        };
        cmbSortEvents.setCellFactory(cellFactory);
        ListCell<String> buttonCell = cellFactory.call(null);
        buttonCell.getStyleClass().add("combo-button-cell");
        cmbSortEvents.setButtonCell(buttonCell);

        cmbSortEvents.getSelectionModel().selectFirst();
        cmbSortEvents.setOnAction(e -> loadEvents());

        listUpcomingEvents.setItems(eventsList);
        listUpcomingEvents.setCellFactory(param -> new EventListCell());

        loadEvents();
    }

    private void setTodayDate() {
        String formatted = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH));
        lblDate.setText(formatted);
    }

    private void loadEvents() {
        eventsList.clear();
        String sortSelection = cmbSortEvents.getValue();
        String sortQuery = "event_date ASC";
        if ("New Additions (Added First)".equals(sortSelection)) {
            sortQuery = "created_date DESC";
        }
        List<Event> events = eventDAO.getUpcomingEvents(DEMO_USER_ID, sortQuery);
        eventsList.addAll(events);
    }

    @FXML
    private void handleMiniTimerClick(MouseEvent event) {
        switchToTab("tabTimer");
    }

    private void switchToTab(String tabFxId) {
        try {
            TabPane tabPane = (TabPane) lblDate.getScene().lookup("#mainTabPane");
            if (tabPane != null) {
                for (Tab tab : tabPane.getTabs()) {
                    if (tabFxId.equals(tab.getId())) {
                        tabPane.getSelectionModel().select(tab);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMiniTimer(String timeStr, boolean isRunning) {
        if (lblMiniTimer != null && miniTimerBox != null) {
            lblMiniTimer.setText(timeStr);
            miniTimerBox.setVisible(isRunning);
            miniTimerBox.setManaged(isRunning);
        }
    }

    public void refresh() {
        loadEvents();
    }

    private class EventListCell extends ListCell<Event> {
        private HBox content;
        private Label typeLabel;
        private VBox textContainer;
        private Text titleText;
        private Text dateText;
        private Button deleteBtn;

        public EventListCell() {
            super();
            typeLabel = new Label();
            typeLabel.getStyleClass().add("event-type-label");
            typeLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
            typeLabel.setTextFill(Color.WHITE);

            titleText = new Text();
            titleText.getStyleClass().add("event-title");
            titleText.setFont(Font.font("System", FontWeight.BOLD, 16));

            dateText = new Text();
            dateText.getStyleClass().add("event-date");
            dateText.setFont(Font.font("System", FontWeight.BOLD, 14));

            textContainer = new VBox(titleText, dateText);
            textContainer.setSpacing(5);
            HBox.setHgrow(textContainer, Priority.ALWAYS);

            deleteBtn = new Button("🗑");
            deleteBtn.getStyleClass().add("todo-delete-button");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 18px;");
            deleteBtn.setOnAction(e -> {
                Event item = getItem();
                if (item != null) {
                    new EventDAO().deleteEvent(item.getId());
                    loadEvents(); // Refresh after deletion
                }
            });

            content = new HBox(typeLabel, textContainer, deleteBtn);
            content.setSpacing(15);
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            content.getStyleClass().add("event-cell");
        }

        @Override
        protected void updateItem(Event item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

            if (empty || item == null) {
                setGraphic(null);
            } else {
                titleText.setText(item.getTitle());
                String dateStr = item.getEventDate() != null ? item.getEventDate().toString() : "";
                String timeStr = item.getEventTime() != null ? item.getEventTime() : "";
                dateText.setText(dateStr + "  " + timeStr);

                typeLabel.setText(item.getType());
                switch (item.getType()) {
                    case "EXAM":
                        typeLabel.setStyle(typeLabel.getStyle() + "-fx-background-color: #e74c3c;");
                        break;
                    case "PROJECT":
                        typeLabel.setStyle(typeLabel.getStyle() + "-fx-background-color: #9b59b6;");
                        break;
                    case "HOMEWORK":
                        typeLabel.setStyle(typeLabel.getStyle() + "-fx-background-color: #f39c12;");
                        break;
                    case "QUIZ":
                        typeLabel.setStyle(typeLabel.getStyle() + "-fx-background-color: #e67e22;");
                        break;
                    default:
                        typeLabel.setStyle(typeLabel.getStyle() + "-fx-background-color: #34495e;");
                        break;
                }
                setGraphic(content);
            }
        }
    }
}