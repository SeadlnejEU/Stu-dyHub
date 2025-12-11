package me.seadlnej.server.requests.profile;

import java.time.LocalDate;

public class UpdateProfileRequest {

    private String image;

    private String address;
    private String birthdate;

    private String bio;

    // Getters and Setter
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

}
