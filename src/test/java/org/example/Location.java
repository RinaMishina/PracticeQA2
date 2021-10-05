package org.example;

import javax.persistence.*;

@Entity(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator")
    private int id;
    private String postal_code;
    private String street_address;
    private String city;
    private String province;
    private int country_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostal_ode() {
        return postal_code;
    }

    public void setPostal_ode(String postal_ode) {
        this.postal_code = postal_ode;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public Location(String postal_ode, String street_address, String city, String province, int country_id) {
        this.postal_code = postal_ode;
        this.street_address = street_address;
        this.city = city;
        this.province = province;
        this.country_id = country_id;
    }

    public Location() {
    }
}
