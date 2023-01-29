package cz.uhk.ppro.album.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class CategoryDto {
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    private String name;
}
