package me.seadlnej.server.requests;

public class RegisterRequest {

    // Register requirements
    private String firstname;
    private String lastname;
    private String username;

    private String email;
    private String phone;
    private String password1;
    private String password2;

    public boolean isEmpty() {
        if (firstname == null || firstname.isBlank() ||
                lastname  == null || lastname.isBlank()  ||
                username  == null || username.isBlank()  ||
                email     == null || email.isBlank()     ||
                phone     == null || phone.isBlank()     ||
                password1 == null || password1.isBlank() ||
                password2 == null || password2.isBlank()) {
            return true;
        }
        return false;
    }

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword1(String password1) { this.password1 = password1; }

    public void setPassword2(String password2) { this.password2 = password2; }

}