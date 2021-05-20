package client.View.Event;

import Shared.Event.Event;
import client.View.ViewHandler;
import client.ViewModel.EventViewModel;
import client.ViewModel.EventListViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventListViewController {
    @FXML
    private TextField searchTextField;
    @FXML
    private TextField ySTextField;
    @FXML
    private TextField mSTextField;
    @FXML
    private TextField dSTextField;
    @FXML
    private TableView<EventViewModel> eventList;
    @FXML
    private TableColumn<EventViewModel, Number> idColumn;
    @FXML
    private TableColumn<EventViewModel, String> titleColumn;
    @FXML
    private TableColumn<EventViewModel, String> descriptionColumn;
    @FXML
    private TableColumn<EventViewModel, String> creatTColumn;
    @FXML
    private TableColumn<EventViewModel, String> dateColumn;
    @FXML
    private TableColumn<EventViewModel, String> endTColumn;
    @FXML
    private TableColumn<EventViewModel, Boolean> isOnlineColumn;
    @FXML
    private TableColumn<EventViewModel, String> platformColumn;
    @FXML
    private TableColumn<EventViewModel, Number> roomColumn;
    @FXML
    private Label errorLabel;
    private int selected;

    private ViewHandler viewHandler;
    private EventListViewModel viewModel;
    private Region root;

    public EventListViewController() {
    }

    public void init(ViewHandler viewHandler, EventListViewModel viewModel, Region root) {
        this.viewHandler = viewHandler;
        this.viewModel = viewModel;
        this.root = root;

        this.selected = -1;
        this.searchTextField.textProperty().bindBidirectional(
                viewModel.getSearchProperty());
        this.ySTextField.textProperty().bindBidirectional(viewModel.getYearSProperty());
        this.mSTextField.textProperty().bindBidirectional(viewModel.getMonthSProperty());
        this.dSTextField.textProperty().bindBidirectional(viewModel.getDaySProperty());

        this.eventList.setItems(viewModel.getEventList());
        idColumn.setCellValueFactory(cellData ->
                cellData.getValue().getIdProperty());
        titleColumn.setCellValueFactory(cellData ->
                cellData.getValue().getTitleProperty());
        descriptionColumn.setCellValueFactory(cellData ->
                cellData.getValue().getDescriptionProperty());
        creatTColumn.setCellValueFactory(cellData ->
                cellData.getValue().getCreateDate());
        dateColumn.setCellValueFactory(cellData ->
                cellData.getValue().getStartDate());
        endTColumn.setCellValueFactory(cellData ->
                cellData.getValue().getEndTime());
        isOnlineColumn.setCellValueFactory(cellData ->
                cellData.getValue().isOnline());
        platformColumn.setCellValueFactory(cellData ->
                cellData.getValue().getPlatformProperty());
        roomColumn.setCellValueFactory(cellData ->
                cellData.getValue().getRoomProperty());
        reset();
    }

    public void reset() {
        errorLabel.setText("Welcome.");
        viewModel.reset();
    }

    public void resetSearch(){
        errorLabel.setText("");
        viewModel.resetSearch();
    }

    public Region getRoot() {
        return root;
    }

    @FXML
    private void refreshPress() {
        viewModel.reset();
        eventList.setItems(viewModel.update());
        errorLabel.setText(viewModel.getEventListSize() + " events loaded.");
    }

    @FXML
    private void searchPress() {
        String dateS = "";
        if (searchTextField.getText() != null && !isValidDate()) {
            eventList.setItems(viewModel.searchExceptDate(searchTextField.getText()));
        } else if (searchTextField.getText() == null && isValidDate()) {
            try {
                if (ySTextField.getText().length() == 4) {
                    dateS += ySTextField.getText();
                    dateS += "-";
                } else throw new IllegalArgumentException("The Year should be 4 digits!");
                if (isValidMonth(mSTextField.getText())) {
                    if (mSTextField.getText().length() == 1) {
                        dateS += "0";
                        dateS += mSTextField.getText();
                        dateS += "-";
                    } else {
                        dateS += mSTextField.getText();
                        dateS += "-";
                    }
                } else throw new IllegalArgumentException("Please enter valid month!");
                if (isValidDay(dSTextField.getText())) {
                    if (dSTextField.getText().length() == 1) {
                        dateS += "0";
                        dateS += dSTextField.getText();
                    } else {
                        dateS += dSTextField.getText();
                    }
                } else throw new IllegalArgumentException("Please enter valid day");
                eventList.setItems(viewModel.searchOnlyDate(dateS));
            } catch (Exception e) {
                errorLabel.setText(e.getMessage());
            }
        } else if (searchTextField.getText() != null && isValidDate()){
            try {
                if (ySTextField.getText().length() == 4) {
                    dateS += ySTextField.getText();
                    dateS += "-";
                } else throw new IllegalArgumentException("The Year should be 4 digits!");
                if (isValidMonth(mSTextField.getText())) {
                    if (mSTextField.getText().length() == 1) {
                        dateS += "0";
                        dateS += mSTextField.getText();
                        dateS += "-";
                    } else {
                        dateS += mSTextField.getText();
                        dateS += "-";
                    }
                } else throw new IllegalArgumentException("Please enter valid month!");
                if (isValidDay(dSTextField.getText())) {
                    if (dSTextField.getText().length() == 1) {
                        dateS += "0";
                        dateS += dSTextField.getText();
                    } else {
                        dateS += dSTextField.getText();
                    }
                } else throw new IllegalArgumentException("Please enter valid day");
                eventList.setItems(viewModel.searchOnlyDate(dateS));
            } catch (Exception e) {
                errorLabel.setText(e.getMessage());
            }
            viewModel.search(searchTextField.getText(), dateS);
        }
    }

    @FXML
    private void addPress() {
        viewHandler.openView("CreateEvent");
    }

    @FXML
    private void editPress() {
        //TODO add edit surface
    }

    @FXML
    private void removePress() {
        if (eventList.getSelectionModel().getSelectedItem() != null) {
            selected = eventList.getSelectionModel().getSelectedItem().getIdProperty().get();
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Confirm");
            a.setHeaderText("Are you sure to delete this event ?");
            a.setContentText(eventList.getSelectionModel().getSelectedItem().getWholeMessage().get());
            Optional<ButtonType> result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                viewModel.removeEvent(selected);
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setHeaderText("Event removed.");
            }
        } else {
            Alert c = new Alert(Alert.AlertType.INFORMATION);
            c.setTitle("Error");
            c.setHeaderText("Please select an event.");
        }
    }

    @FXML
    private void backPress() {
        viewHandler.openView("MainMenu");
    }

    private boolean isValidMonth(String month){
        return (month.equals("1") || month.equals("2") || month.equals("3") || month.equals("4") || month.equals("5") || month.equals("6")
                || month.equals("7") || month.equals("8") || month.equals("9") || month.equals("10") || month.equals("11") || month.equals("12")
                || month.equals("01") || month.equals("02") || month.equals("03") || month.equals("04") || month.equals("05") || month.equals("06")
                || month.equals("07") || month.equals("08") || month.equals("09"));
    }

    private boolean isValidDay(String day){
        return (Integer.valueOf(day) > 0 && (Integer.valueOf(day) <= 31));
    }

    private boolean isValidDate(){
        return ySTextField.getText() != null && mSTextField.getText() != null && dSTextField.getText() != null;
    }
}
