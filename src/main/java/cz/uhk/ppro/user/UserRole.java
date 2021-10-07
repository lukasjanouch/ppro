package cz.uhk.ppro.user;

import com.google.common.collect.Sets;

import java.util.Set;

public enum UserRole {
    USER(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(UserPermission.USER_READ, UserPermission.USER_WRITE,
            UserPermission.ALBUM_READ, UserPermission.ALBUM_WRITE));

    private final Set<UserPermission> permissions;//musí být unikátní, proto set

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }
}
