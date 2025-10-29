package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchProfileController {

    @FXML private TextField searchField;
    @FXML private TableView<StudentProfile> profilesTable;
    @FXML private TableColumn<StudentProfile, String> nameCol;
    @FXML private TableColumn<StudentProfile, String> majorCol;
    @FXML private TableColumn<StudentProfile, String> statusCol;
    @FXML private TableColumn<StudentProfile, String> roleCol;
    @FXML private Label statusLabel;

    private ObservableList<StudentProfile> allProfiles;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        //majorCol.setCellValueFactory(new PropertyValueFactory<>("major"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));

        allProfiles = DataStore.getFullName();
        allProfiles.sort(Comparator.comparing(StudentProfile::getName, String.CASE_INSENSITIVE_ORDER));
        profilesTable.setItems(allProfiles);

        if (allProfiles.isEmpty()) {
            statusLabel.setText("No profiles found.");
        }/* else {
            statusLabel.setText(allProfiles.size() + " profiles loaded.");
        }*/
    }

    @FXML
    protected void onSearch() {
        String keyword;
        if (searchField != null && searchField.getText() != null) {
            keyword = searchField.getText().trim().toLowerCase();
        } else {
            keyword = "";
        }

        if (keyword.isEmpty()) {
            profilesTable.setItems(allProfiles);
            statusLabel.setText("Showing all profiles.");
            return;
        }
        List<StudentProfile> filtered = allProfiles.stream()
                .filter(p ->
                        (p.getName() != null && p.getName().toLowerCase().contains(keyword)) ||
                                (p.getMajor() != null && p.getMajor().toLowerCase().contains(keyword)) ||
                                (p.getAcademicStatus() != null && p.getAcademicStatus().toLowerCase().contains(keyword)) ||
                                (p.getPreferredRole() != null && p.getPreferredRole().toLowerCase().contains(keyword))
                ).collect(Collectors.toList());

        profilesTable.setItems(FXCollections.observableArrayList(filtered));
        if (filtered.isEmpty()) {
            statusLabel.setText("No results found for \"" + keyword + "\".");
        } else {
            statusLabel.setText(filtered.size() + " result(s) found.");
        }
    }

    @FXML
    protected void onClear() {
        searchField.clear();
        profilesTable.setItems(allProfiles);
        statusLabel.setText("Showing all profiles.");
    }

    @FXML
    protected void onDelete() {
        StudentProfile selected = profilesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a profile to delete.", ButtonType.OK);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.showAndWait();
            statusLabel.setText("No profile selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete \"" +
                selected.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            DataStore.deleteByName(selected.getName());
            DataStore.loadProfiles(); // refresh from file
            allProfiles = DataStore.getFullName();
            allProfiles.sort(Comparator.comparing(StudentProfile::getName, String.CASE_INSENSITIVE_ORDER));
            profilesTable.setItems(allProfiles);
            profilesTable.refresh();
            statusLabel.setText("Deleted profile: " + selected.getName());
        } else {
            statusLabel.setText("Deletion cancelled.");
        }
    }

    @FXML
    protected void goBackToHome(ActionEvent e) {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(
                    FXMLLoader.load(getClass().getResource("/cs151/application/home.fxml")),
                    340, 260
            );
            stage.setScene(scene);
            stage.setTitle("KnowledgeTrack Home");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error returning to home page.");
        }
    }

    @FXML
    protected void editPage(ActionEvent event) {
        swapScene(event, "/cs151/application/edit.fxml", 400, 300, "Edit Page");
    }

    //search page
    @FXML
    protected void searchProf(ActionEvent event) {
        swapScene(event, "/cs151/application/search.fxml", 1000, 680, "Search Student Profiles");
    }

    private void swapScene(ActionEvent event, String fxml, int w, int h, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, w, h));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
