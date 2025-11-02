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

    @FXML
    private TextField searchField;
    @FXML
    private TableView<StudentProfile> profilesTable;
    @FXML
    private TableColumn<StudentProfile, String> nameCol;
    @FXML
    private TableColumn<StudentProfile, String> majorCol;
    @FXML
    private TableColumn<StudentProfile, String> statusCol;
    @FXML
    private TableColumn<StudentProfile, String> roleCol;
    @FXML
    private Label statusLabel;


    @FXML
    private ComboBox<String> dropdown, dropDown;
    @FXML
    private RadioButton toggleButton;
    @FXML
    private TextField textField;

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

        //     if (allProfiles.isEmpty()) {
        //          statusLabel.setText("No profiles found.");
        //  }/* else {
        // statusLabel.setText(allProfiles.size() + " profiles loaded.");
        //     }*/
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
        swapScene(event, "/cs151/application/edit.fxml", 626, 450, "Edit Page");
    }

    //search page
    @FXML
    protected void searchProf(ActionEvent event) {
        swapScene(event, "/cs151/application/search.fxml", 1000, 680, "Search Student Profiles");
    }


    private boolean requiredFields() {
        String status = dropdown  != null && dropdown.getValue() != null ? dropdown.getValue().trim() : "";
        String role   = dropDown  != null && dropDown.getValue() != null ? dropDown.getValue().trim() : "";
        boolean employed = toggleButton != null && toggleButton.isSelected();
        String job    = textField != null && textField.getText() != null ? textField.getText().trim() : "";

        if (status.isEmpty()) return error("Academic Status is required.");
        if (role.isEmpty())   return error("Preferred Professional Role is required.");
        if (employed && job.isEmpty()) return error("Job details are required when Employed.");
        return true;
    }
    private boolean error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
        return false;
    }

    @FXML
    private void save() {
        if (!requiredFields()) return;

        var profiles = DataStore.getFullName();
        StudentProfile target = null;
        if (target == null && profiles.size() >= 5) {
            error("Only 5 student profiles are allowed.");
            return;
        }
        final String status   = dropdown.getValue();
        final boolean employed= toggleButton.isSelected();
        final String job      = textField.getText().trim();
        final String role     = dropDown.getValue();

        target.setAcademicStatus(status);
        target.setEmployeed(employed);
        target.setJobDetails(job);
        target.setPreferredRole(role);

        DataStore.saveProfiles();
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
    @FXML
    protected void onEditSelected(ActionEvent event) {
        StudentProfile selected = profilesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a profile to edit.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/edit.fxml"));
            Parent root = loader.load();

            EditController controller = loader.getController();
            controller.loadProfile(selected); // <-- populate edit.fxml

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Edit Profile: " + selected.getName());
            stage.setScene(new Scene(root, 625, 550));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open edit page.").showAndWait();
        }
    }

}