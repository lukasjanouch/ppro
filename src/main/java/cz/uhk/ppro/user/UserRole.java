package cz.uhk.ppro.user;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN
/*    USER(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(USER_READ, USER_WRITE, ALBUM_READ, ALBUM_WRITE)),
    ADMINTRAINEE(Sets.newHashSet(USER_READ, ALBUM_READ));


    private final Set<UserPermission> permissions;//musí být unikátní, proto set

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }*/
}
