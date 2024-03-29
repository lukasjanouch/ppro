package cz.uhk.ppro.user;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.album.like.LikeEntity;
import cz.uhk.ppro.album.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor//will generate a constructor with no parameters
@Entity//pro hybernate, z javax.persistence
public class User implements UserDetails {

    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @Id //z javax.persistence
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    //atributy, které není potřeba ukládat do db označujeme @Transient
    //např. věk, protože lze vypočítat z data narození
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    //@ElementCollection
    //private Set<? extends GrantedAuthority> grantedAuthorities;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Album> albums;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LikeEntity> likeEntities;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked = false;
    private Boolean enabled = false;
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    public User(String username, String email, String password, UserRole userRole) {
                //Set<? extends GrantedAuthority> grantedAuthorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        //this.grantedAuthorities = grantedAuthorities;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
        //return grantedAuthorities;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked; //true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
