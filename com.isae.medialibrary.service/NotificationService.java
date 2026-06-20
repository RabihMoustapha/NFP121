package com.isae.medialibrary.service;

import com.isae.medialibrary.model.Media;
import com.isae.medialibrary.model.Student;

import java.util.List;

public class NotificationService {
    public void notifyStudentsAboutNewMedia(MediaLibrary library, Media media) {
        List<Student> students = library.getAllStudents();
        for (Student s : students) {
            if (s.isInterestedInMedia(media)) {
                System.out.println("EMAIL to " + s.getUsername() + ": New media '" + media.getTitle() + "' available.");
            }
        }
    }
}