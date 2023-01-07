package cz.uhk.ppro.album.comment;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ManyToOne;

@Setter
@Getter
@NoArgsConstructor
public class CommentDto {
    private String text;
}
