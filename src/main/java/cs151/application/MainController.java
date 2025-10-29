package cs151.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;

public class MainController {
    @FXML private Label popUp;
    @FXML private TextField nameField;
    @FXML private TextField languageField;

    @FXML private TableView<ProgrammingLanguages> tableView;
    @FXML private TableView<StudentProfile> nameTable;

    @FXML private TableColumn<ProgrammingLanguages, String> langCol;
    @FXML private TableColumn<StudentProfile, String> nameCol;
    @FXML private ListView<String> languagesList;

    @FXML private ComboBox<String> dropdown, dropDown;
    @FXML private RadioButton toggleButton;
    @FXML private TextField textField;

    @FXML private TableColumn<StudentProfile, String> statusCol, empCol, roleCol;
    @FXML
    private ListView<String> multiSelectListView;

    @FXML
    public void initializer() {
        ObservableList<String> items = FXCollections.observableArrayList("MySQL", "Postgres", "MongoDB");
        multiSelectListView.setItems(items);

        multiSelectListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void initialize() {
        if (tableView != null) {
            langCol.setCellValueFactory(new PropertyValueFactory<>("programmingLanguage"));
            ObservableList<ProgrammingLanguages> langs =
                    FXCollections.observableArrayList(DataStore.getList());
            langs.sort(Comparator.comparing(
                    ProgrammingLanguages::getProgrammingLanguage, String.CASE_INSENSITIVE_ORDER));
            tableView.setItems(langs);
            langCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
        }
        if (languagesList != null) {
            ObservableList<String> opts = FXCollections.observableArrayList(
                    DataStore.getList().stream()
                            .map(ProgrammingLanguages::getProgrammingLanguage).toList()
            );
            languagesList.setItems(opts);
            languagesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        if (multiSelectListView != null) {
            multiSelectListView.setItems(FXCollections.observableArrayList("MySQL", "Postgres", "MongoDB"));
            multiSelectListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        if (nameTable != null && nameCol != null) {
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            //statusCol.setCellValueFactory(new PropertyValueFactory<>("academicStatus"));
            /*empCol.setCellValueFactory(cell ->
                    new javafx.beans.property.SimpleStringProperty(
                            cell.getValue().isEmployed() ? "Employed" : "Not Employed"));
            roleCol.setCellValueFactory(new PropertyValueFactory<>("preferredRole"));*/
            nameTable.setItems(DataStore.getFullName());
            nameTable.getItems().sort(java.util.Comparator.comparing(
                    StudentProfile::getName, String.CASE_INSENSITIVE_ORDER));
        }
    }

    @FXML
    private void initializeProf() {
        if (nameTable != null) {
            nameCol.setCellValueFactory(new PropertyValueFactory<>("Student Profile"));

            ObservableList<StudentProfile> fullName = FXCollections.observableArrayList(DataStore.getFullName());
            Comparator<StudentProfile> nameCompare = Comparator.comparing
                    (StudentProfile::getName, String.CASE_INSENSITIVE_ORDER);

            fullName.sort(nameCompare);

            nameTable.setItems(fullName);
            nameCol.prefWidthProperty().bind(nameTable.widthProperty().multiply(0.5));
        }
    }

    //front page
    @FXML
    protected void onNavigateButtonClick(ActionEvent event) {
        swapScene(event, "/cs151/application/hello-view.fxml", 320, 240, "KnowledgeTrack");
    }
    //homepage, contains: define lang, profile, back to front
    @FXML
    private void goBackToHome(ActionEvent event) {
        swapScene(event, "/cs151/application/home.fxml", 340, 260, "KnowledgeTrack Home");
    }
    //saved lang table, contains: table, back to define lang
    @FXML
    protected void programmingLanguagesTable(ActionEvent event){
        swapScene(event, "/cs151/application/program_table.fxml", 400, 300, "Saved Languages");
    }
    //student profile, add name, contains: save, edit, view saved profiles, back to home
    @FXML
    protected void studentProfile(ActionEvent event) {
        swapScene(event, "/cs151/application/student.fxml", 600, 400, "Student Profile");
    }
    //saved profiles, contains: list of profiles, back to making student profile
    @FXML
    protected void savedProfile(ActionEvent event) {
        swapScene(event, "/cs151/application/saved_profile.fxml", 400, 300, "Saved Profiles");
    }
    //define languages, add langs, contains: save, edit, view saved langs, back to home
    @FXML
    protected void programmingLang(ActionEvent event) {
        swapScene(event, "/cs151/application/programming_languages.fxml", 640, 420, "Programming Languages");
    }

    //search page
    @FXML
    protected void searchProf(ActionEvent event) {
        swapScene(event, "/cs151/application/search.fxml", 1000, 680, "Search Student Profiles");
    }

    private boolean requiredFields() {
        String name   = nameField != null && nameField.getText() != null ? nameField.getText().trim() : "";
        String status = dropdown  != null && dropdown.getValue() != null ? dropdown.getValue().trim() : "";
        String role   = dropDown  != null && dropDown.getValue() != null ? dropDown.getValue().trim() : "";
        boolean employed = toggleButton != null && toggleButton.isSelected();
        String job    = textField != null && textField.getText() != null ? textField.getText().trim() : "";

        if (name.isEmpty())   return error("Full Name is required.");
        if (status.isEmpty()) return error("Academic Status is required.");
        if (role.isEmpty())   return error("Preferred Professional Role is required.");
        if (employed && job.isEmpty()) return error("Job details are required when Employed.");
        return true;
    }
    private boolean error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
        return false;
    }

    //saves languages
    @FXML
    private void onSave() {
        if (languageField == null) return;

        var langsList = DataStore.getList();
        if (langsList.size() >= 3) {
            error("Only 3 programming languages are allowed.");
            return;
        }
        String lang  = languageField.getText() == null ? "" : languageField.getText().trim();
        if (lang.isEmpty()) return;
        boolean duplicate = langsList.stream().anyMatch(pl ->
                pl.getProgrammingLanguage() != null &&
                        pl.getProgrammingLanguage().equalsIgnoreCase(lang));
        if (duplicate) {
            error("Language already exists.");
            return;
        }
        langsList.add(new ProgrammingLanguages(lang));
        DataStore.save();
        languageField.clear();
    }

    //saves profile
    @FXML
    private void save() {
        if (!requiredFields()) return;
        final String name     = nameField.getText().trim();

        var profiles = DataStore.getFullName();
        StudentProfile target = null;
        for (StudentProfile sp : profiles) {
            if (sp.getName() != null && sp.getName().equalsIgnoreCase(name)) {
                target = sp; break;
            }
        }
        if (target == null && profiles.size() >= 5) {
            error("Only 5 student profiles are allowed.");
            return;
        }
        final String status   = dropdown.getValue();
        final boolean employed= toggleButton.isSelected();
        final String job      = textField.getText().trim();
        final String role     = dropDown.getValue();

        if (target == null) {
            target = new StudentProfile(name);
            DataStore.getFullName().add(target);
        }
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


}