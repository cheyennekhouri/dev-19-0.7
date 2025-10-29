package cs151.application;
import java.util.ArrayList;
import java.util.List;
public class StudentProfile  implements Comparable<StudentProfile> {
    private String name;
    private String major;
    private List<String> languages;
    private String academicStatus;
    private boolean employed;
    private String jobDetails;
    private String preferredRole;
    private String comments;
    private boolean whiteList;
    private boolean blackList;

    public StudentProfile(){
        this.name = "";
        this.major = "";
        this.languages = new ArrayList<>();
        this.academicStatus = "";
        this.jobDetails = "";
        this.preferredRole = "";
        this.comments = "";
        this.whiteList = false;
        this.blackList = false;
    }
    public StudentProfile(String name, String major, List<String> languages){
        setName(name);
        setMajor(major);
        setLanguages(languages);
    }

    public StudentProfile(String name){
        this();
        setName(name);
    }
    // Name
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    // Major
    public void setMajor(String major) {
        this.major = major;
    }
    public String getMajor() {
        return major;
    }
    // Languages
    public List<String> getLanguages() {
        return languages;
    }
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    // dropdown
    public String getAcademicStatus() {
        return academicStatus;
    }
    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }
    // radio
    public boolean isEmployed() {
        return employed;
    }
    public void setEmployeed(boolean employed) {
        this.employed = employed;
    }

    public String getJobDetails() {
        return jobDetails;
    }
    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }
    // Dropdown
    public String getPreferredRole() {
        return preferredRole;
    }
    public void setPreferredRole(String preferredRole) {
        this.preferredRole = preferredRole;
    }
    // Text
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    // WhiteList
    public boolean isWhiteList() {
        return whiteList;
    }
    public void setWhiteList(boolean whiteList){
        if(whiteList){
            this.blackList = false;
        }
        this.whiteList = whiteList;
    }
    // Blacklist
    public boolean isBlackList() {
        return blackList;
    }
    public void setBlackList(boolean blackList) {
        if(blackList){
            this.whiteList = false;
        }
        this.blackList = blackList;
    }
    public String getLanguagesString(){
        return String.join(" | ", languages);
    }
    //compare names to sort
    @Override
    public int compareTo(StudentProfile other){
        return this.name.compareToIgnoreCase(other.name);
    }
    @Override
    public String toString(){
        return String.join(",", name, major, academicStatus, employed ? "Employed" : "Not Employed",
                jobDetails, String.join("|", languages), preferredRole,
                comments.replace(",", ";"), String.valueOf(whiteList), String.valueOf(blackList));
    }
}