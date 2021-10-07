package cz.uhk.ppro.user;

public enum UserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    ALBUM_READ("album:read"),
    ALBUM_WRITE("album:write");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
