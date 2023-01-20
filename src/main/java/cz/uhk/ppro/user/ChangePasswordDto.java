package cz.uhk.ppro.user;

import cz.uhk.ppro.validation.PasswordMatches;
import cz.uhk.ppro.validation.ValidEmail;
import cz.uhk.ppro.validation.ValidPassword;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@PasswordMatches(message = "Hesla se neshodují.")
public class ChangePasswordDto {

    @NotNull
    @NotEmpty(message = "Toto pole být vyplněno.")
    @ValidPassword
    private String password;
    @NotNull
    @NotEmpty(message = "Toto pole být vyplněno.")
    private String matchingPassword;







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



    // standard getters and setters
}
