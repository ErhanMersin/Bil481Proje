package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.dao.EventDAO;
import com.fellow.app.model.Course;
import com.fellow.app.model.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendarController {

    @FXML
    private Label lblMonthYear;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label lblSelectedDate;
    @FXML
    private ListView<Event> listDailyEvents;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    private final int DEMO_USER_ID = 1;
    private EventDAO eventDAO = new EventDAO();
    private CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();

        listDailyEvents.setCellFactory(param -> new DailyEventCell());

        populateCalendar(currentYearMonth);
        loadAgenda(selectedDate);
    }

    public void refresh() {
        if (currentYearMonth == null) {
            currentYearMonth = YearMonth.now();
        }
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        populateCalendar(currentYearMonth);
        loadAgenda(selectedDate);
    }

    private void populateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();

        lblMonthYear.setText(yearMonth.getMonth().name() + " " + yearMonth.getYear());

        // Ensure standard row heights
        calendarGrid.getRowConstraints().clear();
        for (int i = 0; i < 6; i++) {
            javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
            rc.setPercentHeight(16.66);
            rc.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rc);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekOfFirst = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Fetch all events for this month tracking
        List<Event> monthEvents = eventDAO.getEventsByMonth(DEMO_USER_ID, yearMonth.getYear(),
                yearMonth.getMonthValue());

        int col = dayOfWeekOfFirst - 1; // 0-indexed for Monday
        int row = 0;

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate cellDate = yearMonth.atDay(day);
            VBox dayCell = createDayCell(cellDate, monthEvents);

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createDayCell(LocalDate date, List<Event> monthEvents) {
        VBox cell = new VBox();
        cell.setAlignment(Pos.TOP_LEFT);
        cell.getStyleClass().add("calendar-day-cell");

        boolean isSelected = date.equals(selectedDate);
        boolean isToday = date.equals(LocalDate.now());

        if (isSelected) {
            cell.getStyleClass().add("calendar-day-cell-selected");
        } else if (isToday) {
            cell.getStyleClass().add("calendar-day-cell-today");
        }

        Label lblDay = new Label(String.valueOf(date.getDayOfMonth()));
        lblDay.getStyleClass().add("calendar-day-number");
        if (isToday && !isSelected) {
            lblDay.getStyleClass().add("today-day-number");
        }

        // Event dots
        HBox dotsBox = new HBox(3);
        dotsBox.setAlignment(Pos.BOTTOM_RIGHT);

        int eventCount = 0;
        for (Event e : monthEvents) {
            if (e.getEventDate() != null && e.getEventDate().equals(date)) {
                if (eventCount < 4) {
                    javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(4);
                    switch (e.getType()) {
                        case "EXAM":
                            dot.setFill(Color.web("#e74c3c"));
                            break;
                        case "PROJECT":
                            dot.setFill(Color.web("#9b59b6"));
                            break;
                        case "HOMEWORK":
                            dot.setFill(Color.web("#f39c12"));
                            break;
                        case "QUIZ":
                            dot.setFill(Color.web("#e67e22"));
                            break;
                        default:
                            dot.setFill(Color.web("#34495e"));
                            break;
                    }
                    dotsBox.getChildren().add(dot);
                }
                eventCount++;
            }
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        cell.getChildren().addAll(lblDay, spacer, dotsBox);

        // Click Selection
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            populateCalendar(currentYearMonth);
            loadAgenda(date);
        });

        return cell;
    }

    private void loadAgenda(LocalDate date) {
        lblSelectedDate.setText(date.getMonth().name() + " " + date.getDayOfMonth() + ", " + date.getYear());
        List<Event> dailyEvents = eventDAO.getEventsByDate(DEMO_USER_ID, date);
        listDailyEvents.getItems().setAll(dailyEvents);
    }

    private boolean isDarkMode() {
        Scene scene = calendarGrid != null ? calendarGrid.getScene() : null;
        if (scene == null) {
            return true;
        }
        return scene.getStylesheets().stream().anyMatch(s -> s.contains("dark-theme.css"));
    }

    private String getModeAdjustedCourseColor(String baseHex, boolean darkMode) {
        if (baseHex == null || baseHex.isBlank()) {
            return darkMode ? "#DDDDDD" : "#333333";
        }

        Color baseColor;
        try {
            baseColor = Color.web(baseHex);
        } catch (Exception e) {
            return darkMode ? "#DDDDDD" : "#333333";
        }

        double hue = baseColor.getHue();
        double sat = baseColor.getSaturation();
        double bright = baseColor.getBrightness();

        double targetBrightness = darkMode
                ? Math.min(1.0, bright + 0.35)
                : Math.max(0.0, bright - 0.35);

        Color adjusted = Color.hsb(hue, sat, targetBrightness);
        return String.format("#%02X%02X%02X",
                (int) Math.round(adjusted.getRed() * 255),
                (int) Math.round(adjusted.getGreen() * 255),
                (int) Math.round(adjusted.getBlue() * 255));
    }

    @FXML
    public void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    @FXML
    public void handlePrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    @FXML
    public void handleAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEventDialog.fxml"));
            Parent root = loader.load();

            AddEventController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Add New Event");
            Scene dialogScene = new Scene(root);

            Scene parentScene = calendarGrid.getScene();
            if (parentScene != null) {
                dialogScene.getStylesheets().addAll(parentScene.getStylesheets());
            }

            stage.setScene(dialogScene);

            // Auto select current date if valid
            if (selectedDate != null) {
                controller.setDate(selectedDate);
            }

            stage.showAndWait();

            // Refresh after dialog closes
            populateCalendar(currentYearMonth);
            if (selectedDate != null)
                loadAgenda(selectedDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DailyEventCell extends ListCell<Event> {
        private HBox content;
        private Label typeLabel;
        private VBox textContainer;
        private Text titleText;
        private Text courseText;
        private Text timeText;

        public DailyEventCell() {
            super();
            typeLabel = new Label();
            typeLabel.setStyle(
                    "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 5; -fx-font-size: 10px;");
            typeLabel.setTextFill(Color.WHITE);

            titleText = new Text();
            titleText.getStyleClass().add("daily-event-title");
            titleText.setFont(Font.font("System", FontWeight.BOLD, 14));

            courseText = new Text();
            courseText.getStyleClass().add("daily-event-course");
            courseText.setFont(Font.font("System", 12));

            timeText = new Text();
            timeText.getStyleClass().add("daily-event-time");
            timeText.setFont(Font.font("System", 12));

            textContainer = new VBox(titleText, courseText, timeText);
            textContainer.setSpacing(3);
            HBox.setHgrow(textContainer, Priority.ALWAYS);

            content = new HBox(textContainer, typeLabel);
            content.setSpacing(10);
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            content.getStyleClass().add("daily-event-cell");
        }

        @Override
        protected void updateItem(Event item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

            if (empty || item == null) {
                setGraphic(null);
            } else {
                boolean darkMode = isDarkMode();

                titleText.setText(item.getTitle());
                String titleColor = darkMode ? "#FFFFFF" : "#000000";
                titleText.setStyle("-fx-fill: " + titleColor + ";");
                timeText.setText((item.getEventTime() != null && !item.getEventTime().isEmpty()) ? item.getEventTime()
                        : "All Day");

                Course course = courseDAO.getCourseById(item.getCourseId());
                if (course != null) {
                    courseText.setText(course.getCourseName());
                    String colorHex = getModeAdjustedCourseColor(course.getColorHex(), darkMode);
                    courseText.setStyle("-fx-fill: " + colorHex + ";");
                } else {
                    courseText.setText("Default");
                    String fallback = darkMode ? "#DDDDDD" : "#333333";
                    courseText.setStyle("-fx-fill: " + fallback + ";");
                }

                typeLabel.setText(item.getType());
                switch (item.getType()) {
                    case "EXAM":
                        typeLabel.setStyle(
                                typeLabel.getStyle() + "-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        break;
                    case "PROJECT":
                        typeLabel.setStyle(
                                typeLabel.getStyle() + "-fx-background-color: #9b59b6; -fx-text-fill: white;");
                        break;
                    case "HOMEWORK":
                        typeLabel.setStyle(
                                typeLabel.getStyle() + "-fx-background-color: #f39c12; -fx-text-fill: white;");
                        break;
                    case "QUIZ":
                        typeLabel.setStyle(
                                typeLabel.getStyle() + "-fx-background-color: #e67e22; -fx-text-fill: white;");
                        break;
                    default:
                        typeLabel.setStyle(
                                typeLabel.getStyle() + "-fx-background-color: #34495e; -fx-text-fill: white;");
                        break;
                }
                setGraphic(content);
            }
        }
    }
}
