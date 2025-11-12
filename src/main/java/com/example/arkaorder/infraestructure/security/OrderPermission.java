package com.example.arkaorder.infraestructure.security;


import com.example.arkaorder.domain.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderPermission")
@RequiredArgsConstructor
public class OrderPermission {

    private final OrderRepositoryPort orders;
    private final UserContext userContext;

    private boolean hasRole(Authentication auth, String role) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private boolean isAdmin(Authentication auth) {
        return hasRole(auth, "ADMIN");
    }

    private Long currentUserId(Authentication auth) {
        Long fromHeader = userContext.currentUserIdOrNull();
        if (fromHeader != null) return fromHeader;
        if (auth != null && auth.getName() != null) {
            try { return Long.valueOf(auth.getName()); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    public boolean canCreate(Authentication auth) {
        return hasRole(auth, "USER") || isAdmin(auth);
    }

    public boolean canAccess(Authentication auth, Long orderId) {
        if (isAdmin(auth)) return true;
        Long me = currentUserId(auth);
        if (me == null) return false;
        return orders.findById(orderId)
                .map(o -> me.equals(o.getClientId()))
                .orElse(false);
    }

    public boolean canList(Authentication auth) {
        return hasRole(auth, "USER") || isAdmin(auth);
    }

    public boolean canFilterByStatus(Authentication auth) {
        return isAdmin(auth);
    }
}
