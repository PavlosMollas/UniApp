/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Objects;

@Entity
//Όνομα πίνακα στη βάση δεδομένων
@Table(name = "UNINFO")
public class Uninfo implements Serializable {
    
    //Το πεδίο name είναι πρωτεύον κλειδί της οντότητας
    @Id
    @Column(name = "name")  
    private String name;
    @Column(name = "country")  
    private String country;

    //Σχέση OneToOne με την οντότητα University
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({
        @JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false),
        @JoinColumn(name = "country", referencedColumnName = "country", insertable = false, updatable = false)
    })
    private University university;

    @Column(name = "comments")
    private String comments;

    @Column(name = "department")
    private String department;

    @Column(name = "communication")
    private String communication;


    public Uninfo() {}

    //Constructor με ορίσματα το όνομα και τη χώρα
    public Uninfo(String name, String country) {
        this.name = name;
        this.country = country;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uninfo unInfo = (Uninfo) o;
        return university.equals(unInfo.university);
    }

    @Override
    public int hashCode() {
        return Objects.hash(university);
    }
}