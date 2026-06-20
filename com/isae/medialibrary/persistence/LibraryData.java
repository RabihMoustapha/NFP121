package com.isae.medialibrary.persistence;

import com.isae.medialibrary.model.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "issae")
@XmlAccessorType(XmlAccessType.FIELD)
public class LibraryData {
    @XmlElement(name = "specialite")
    private List<Specialty> specialties = new ArrayList<>();

    @XmlElementWrapper(name = "administrateurs")
    @XmlElement(name = "administrateur")
    private List<Administrator> administrators = new ArrayList<>();

    @XmlElementWrapper(name = "mediatheque")
    @XmlElement(name = "media")
    private List<Media> mediaList = new ArrayList<>();

    public LibraryData() {}

    public List<Specialty> getSpecialties() { return specialties; }
    public void setSpecialties(List<Specialty> specialties) { this.specialties = specialties; }
    public List<Administrator> getAdministrators() { return administrators; }
    public void setAdministrators(List<Administrator> administrators) { this.administrators = administrators; }
    public List<Media> getMediaList() { return mediaList; }
    public void setMediaList(List<Media> mediaList) { this.mediaList = mediaList; }
}