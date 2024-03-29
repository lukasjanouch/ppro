package cz.uhk.ppro.user;

import cz.uhk.ppro.validation.PasswordMatches;
import cz.uhk.ppro.validation.ValidPassword;
import cz.uhk.ppro.validation.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordMatches(message = "Hesla se neshodují.")
public class UserDto {
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    private String username;

    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    @ValidEmail
    private String email;
    @NotNull
    @NotEmpty(message = "Toto pole být vyplněno.")
    @ValidPassword
    private String password;
    @NotNull
    @NotEmpty(message = "Toto pole být vyplněno.")
    private String matchingPassword;



    public UserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // standard getters and setters
}

