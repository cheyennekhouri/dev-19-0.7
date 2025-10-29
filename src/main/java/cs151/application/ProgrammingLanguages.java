package cs151.application;

import java.util.Comparator;

public class ProgrammingLanguages implements Comparable<ProgrammingLanguages> {
    private String programmingLanguage;

    // constructor
    public ProgrammingLanguages(){
        this.programmingLanguage = "";
    }
    public ProgrammingLanguages(String programmingLanguage){
        setProgrammingLanguage(programmingLanguage);
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }
    //compare languages to sort
    @Override
    public int compareTo(ProgrammingLanguages other){
        return this.programmingLanguage.compareToIgnoreCase(other.programmingLanguage);
    }

    @Override
    public String toString(){
        return "Programming Language: " + this.programmingLanguage;
    }
}