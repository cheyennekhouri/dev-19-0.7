package cs151.application;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class EditController {

    @FXML private Label lblName;
    @FXML private ComboBox<String> cbAcademicStatus;
    @FXML private RadioButton rbEmployed;
    @FXML private TextField tfJobDetails;
    @FXML private ComboBox<String> cbPreferredRole;
    @FXML private ListView<String> lvProgrammingLanguages;
    @FXML private TextArea taComments;
    @FXML private CheckBox cbWhitelist;
    @FXML private CheckBox cbBlacklist;

    private StudentProfile current;

    @FXML
    public void initialize() {
        if (cbAcademicStatus != null) {
            cbAcademicStatus.setItems(FXCollections.observableArrayList(
                    "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
            ));
        }
        if (cbPreferredRole != null) {
            cbPreferredRole.setItems(FXCollections.observableArrayList(
                    "Front-End", "Back-End", "Full-Stack", "Data", "Other"
            ));
        }
        if (lvProgrammingLanguages != null) {
            List<String> langs = DataStore.getList().stream()
                    .map(ProgrammingLanguages::getProgrammingLanguage)
                    .collect(Collectors.toList());
            lvProgrammingLanguages.setItems(FXCollections.observableArrayList(langs));
            lvProgrammingLanguages.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        if (cbWhitelist != null && cbBlacklist != null) {
            cbWhitelist.setOnAction(e -> {
                if (cbWhitelist.isSelected()) cbBlacklist.setSelected(false);
            });
            cbBlacklist.setOnAction(e -> {
                if (cbBlacklist.isSelected()) cbWhitelist.setSelected(false);
            });
        }
    }

    public void loadProfile(StudentProfile p) {
        this.current = p;

        // Load read-only fields
        lblName.setText(nvl(p.getName()));

        // Load editable fields with a safe check!
        if (cbAcademicStatus != null) {
            cbAcademicStatus.setValue(p.getAcademicStatus());
        }

        rbEmployed.setSelected(p.isEmployed());
        tfJobDetails.setText(nvl(p.getJobDetails()));

        if (cbPreferredRole != null) {
            cbPreferredRole.setValue(p.getPreferredRole());
        }

        // Select the student's programming languages
        if (p.getLanguages() != null) {
            lvProgrammingLanguages.getSelectionModel().clearSelection();
            for (String lang : p.getLanguages()) {
                int index = lvProgrammingLanguages.getItems().indexOf(lang);
                if (index >= 0) {
                    lvProgrammingLanguages.getSelectionModel().select(index);
                }
            }
        }

        taComments.setText(nvl(p.getComments()));
        cbWhitelist.setSelected(p.isWhiteList());
        cbBlacklist.setSelected(p.isBlackList());

        System.out.println("[Edit] Loaded: " + p.getName());
    }

    @FXML
    private void save() {
        if (current == null) {
            new Alert(Alert.AlertType.ERROR, "No profile loaded.").showAndWait();
            return;
        }

        // Save all editable fields
        if (cbAcademicStatus.getValue() != null)
            current.setAcademicStatus(cbAcademicStatus.getValue());

        current.setEmployeed(rbEmployed.isSelected());

        if (tfJobDetails.getText() != null)
            current.setJobDetails(tfJobDetails.getText().trim());

        if (cbPreferredRole.getValue() != null)
            current.setPreferredRole(cbPreferredRole.getValue());

        // Save selected programming languages
        List<String> selectedLangs = lvProgrammingLanguages.getSelectionModel().getSelectedItems();
        current.setLanguages(selectedLangs);

        current.setComments(taComments.getText() == null ? "" : taComments.getText().trim());
        current.setWhiteList(cbWhitelist.isSelected());
        current.setBlackList(cbBlacklist.isSelected());

        DataStore.replaceByName(current);

        new Alert(Alert.AlertType.INFORMATION, "Saved changes for: " + current.getName()).showAndWait();
    }

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
            new Alert(Alert.AlertType.ERROR, "Navigation error").showAndWait();
        }
    }

    private static String nvl(String s) {
        return (s == null || s.isBlank()) ? "" : s;
    }
}