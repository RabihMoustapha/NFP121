package com.isae.medialibrary.service.filter;

import com.isae.medialibrary.model.Media;
import com.isae.medialibrary.model.Subject;

public class SubjectFilter implements FilterCriteria {
    private Subject subject;
    public SubjectFilter(Subject subject) { this.subject = subject; }
    @Override
    public boolean matches(Media media) {
        return media.getSubjects().contains(subject);
    }
}