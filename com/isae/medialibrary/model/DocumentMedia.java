package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentMedia extends Media {
    @XmlAttribute(name = "pageCount")
    private int pageCount;

    public DocumentMedia() {}
    public DocumentMedia(String id, String title, String author, int year, String desc, int pages) {
        super(id, title, author, year, desc);
        this.pageCount = pages;
    }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    @Override public String getType() { return "Document"; }
    @Override public String getSpecificDetails() { return "Pages: " + pageCount; }
}