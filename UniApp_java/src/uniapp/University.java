/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
//Όνομα πίνακα στη βάση δεδομένων
@Table(name = "UNIVERSITY")
//Χρησιμοποιεί μια ξεχωριστή κλάση για το σύνθετο πρωτεύον κλειδί
@IdClass(UniversityId.class)                                                    
public class University implements Serializable {

    //Το πεδίο name είναι μέρος του πρωτεύοντος κλειδιού της οντότητας
    @Id
    @Column(name = "name")
    private String name;

    //Το πεδίο country είναι μέρος του πρωτεύοντος κλειδιού της οντότητας
    @Id
    @Column(name = "country")
    private String country;

    @Column(name = "domains")
    private String domains;

    @Column(name = "web_pages")
    private String webPages;

    @Column(name = "alpha_two_code")
    private String alphaTwoCode;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "searches")
    private Integer searches;

    @OneToOne(mappedBy = "university", cascade = CascadeType.PERSIST)
    private Uninfo uninfo;

    public University() {}

    //Constructor με ορίσματα το όνομα και τη χώρα
    public University(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uninfo getUninfo() {
        return uninfo;
    }

    public void setUninfo(Uninfo uninfo) {
        this.uninfo = uninfo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDomains() {
        return domains;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    public String getWebPages() {
        return webPages;
    }

    public void setWebPages(String webPages) {
        this.webPages = webPages;
    }

    public String getAlphaTwoCode() {
        return alphaTwoCode;
    }

    public void setAlphaTwoCode(String alphaTwoCode) {
        this.alphaTwoCode = alphaTwoCode;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public Integer getSearches() {
        return searches;
    }

    public void setSearches(Integer searches) {
        this.searches = searches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        University that = (University) o;
        return name.equals(that.name) && country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }
}
