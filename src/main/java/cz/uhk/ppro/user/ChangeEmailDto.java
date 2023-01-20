package cz.uhk.ppro.user;

import cz.uhk.ppro.validation.ValidEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class ChangeEmailDto {
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    @ValidEmail
    private String email;
}
