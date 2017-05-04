package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roberto on 28/04/17.
 */

public class User implements Serializable {

    @SerializedName("id_user")
    private int idUser;
    @SerializedName("id_country")
    private String idCountry;
    @SerializedName("id_city")
    private int idCity;
    @SerializedName("name")
    private String name;
    @SerializedName("first_last_name")
    private String firstLastName;
    @SerializedName("email")
    private String email;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("password")
    private String password;
    @SerializedName("register_date")
    private String registerDate;
    @SerializedName("approved")
    private String approved;
    @SerializedName("newsletters")
    private String newsletters;
    @SerializedName("id_state")
    private int idState;
    @SerializedName("image")
    private String image;
    @SerializedName("phone")
    private String phone;
    @SerializedName("second_last_name")
    private String secondLastName;
    @SerializedName("valoracion")
    private String valoracion;

    private Error error;

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }

    public int getIdCity() {
        return idCity;
    }

    public void setIdCity(int idCity) {
        this.idCity = idCity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getNewsletters() {
        return newsletters;
    }

    public void setNewsletters(String newsletters) {
        this.newsletters = newsletters;
    }

    public int getIdState() {
        return idState;
    }

    public void setIdState(int idState) {
        this.idState = idState;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
