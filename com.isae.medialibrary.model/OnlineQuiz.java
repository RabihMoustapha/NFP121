package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class OnlineQuiz extends Media {
    @XmlAttribute(name = "duration")
    private int estimatedDuration;
    @XmlAttribute(name = "difficulty")
    private String difficultyLevel;

    public OnlineQuiz() {}
    public OnlineQuiz(String id, String title, String author, int year, String desc, int duration, String difficulty) {
        super(id, title, author, year, desc);
        this.estimatedDuration = duration;
        this.difficultyLevel = difficulty;
    }
    public int getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(int d) { estimatedDuration = d; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String d) { difficultyLevel = d; }
    @Override public String getType() { return "Online Quiz"; }
    @Override public String getSpecificDetails() { return "Duration: " + estimatedDuration + " min, Difficulty: " + difficultyLevel; }
}