package cz.uhk.ppro.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ChangeUsernameDto {
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    private String username;
}
