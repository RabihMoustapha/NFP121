package com.isae.medialibrary.service.factory;

import com.isae.medialibrary.model.Media;

public interface MediaFactory {
    Media createMedia(String id, String title, String author, int year, String description, Object... params);
}