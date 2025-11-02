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

public class EditController {

    @FXML private Label lblName;
    @FXML private Label lblAcademicStatus;
    @FXML private Label lblCurrentJob;

    @FXML private TextArea taJobDetails;

    @FXML private ListView<String> lvAchievements;
    @FXML private ListView<String> lvSkills;
    @FXML private ListView<String> lvCareerGoals;
    @FXML private ListView<String> lvProgrammingLanguages;

    @FXML private TextArea taComments;
    @FXML private CheckBox cbWhitelist;
    @FXML private CheckBox cbBlacklist;

    @FXML private ComboBox<String> academicStatusDropDown;
    @FXML private ComboBox<String> preferredRoleDropDown;
    @FXML private TextField jobDetailsField;

    private StudentProfile current;

    @FXML
    public void initialize() {
        if (lvAchievements != null)     lvAchievements.setItems(FXCollections.observableArrayList());
        if (lvSkills != null)           lvSkills.setItems(FXCollections.observableArrayList());
        if (lvCareerGoals != null)      lvCareerGoals.setItems(FXCollections.observableArrayList());
        if (lvProgrammingLanguages != null)
            lvProgrammingLanguages.setItems(FXCollections.observableArrayList());
        if(academicStatusDropDown != null)
            academicStatusDropDown.getItems().addAll("Freshman", "Sophomore", "Junior", "Senior", "Graduate");
        if(preferredRoleDropDown != null)
            preferredRoleDropDown.getItems().addAll("Front-End", "Back-End", "Full-Stack", "Data", "Other");
    }

    public void loadProfile(StudentProfile p) {
        this.current = p;

        lblName.setText(nvl(p.getName()));
        lblAcademicStatus.setText(nvl(p.getAcademicStatus()));
        lblCurrentJob.setText(p.isEmployed() ? "Employed" : "Not Employed");
        academicStatusDropDown.setValue(p.getAcademicStatus());
        jobDetailsField.setText(p.getJobDetails());
        preferredRoleDropDown.setValue(p.getPreferredRole());

        taJobDetails.setText(nvl(p.getJobDetails()));

        List<String> langs = (p.getLanguages() == null) ? List.of() : p.getLanguages();
        lvProgrammingLanguages.setItems(FXCollections.observableArrayList(langs));

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
        if (academicStatusDropDown != null && academicStatusDropDown.getValue() != null)
            current.setAcademicStatus(academicStatusDropDown.getValue());
        if (jobDetailsField != null && jobDetailsField.getText() != null)
            current.setJobDetails(jobDetailsField.getText().trim());
        if (preferredRoleDropDown != null && preferredRoleDropDown.getValue() != null)
            current.setPreferredRole(preferredRoleDropDown.getValue());
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

    private static String nvl(String s) { return (s == null || s.isBlank()) ? "—" : s; }
}
