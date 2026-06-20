package com.isae.medialibrary.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private String code;
    @XmlAttribute
    private String name;
    @XmlTransient
    private Specialty specialty;

    public Subject() {}
    public Subject(String code, String name, Specialty specialty) {
        this.code = code;
        this.name = name;
        this.specialty = specialty;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        return code.equals(((Subject) o).code);
    }
    @Override
    public int hashCode() { return code.hashCode(); }
    @Override
    public String toString() { return code + " - " + name; }
}