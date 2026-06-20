package com.isae.medialibrary.service.filter;

import com.isae.medialibrary.model.Media;

public interface FilterCriteria {
    boolean matches(Media media);
}