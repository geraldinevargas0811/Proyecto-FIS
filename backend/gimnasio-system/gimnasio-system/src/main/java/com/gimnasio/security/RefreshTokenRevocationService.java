package com.gimnasio.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenRevocationService {

    private final Set<String> revokedRefreshTokenIds = ConcurrentHashMap.newKeySet();

    public void revoke(String tokenId) {
        if (tokenId != null && !tokenId.isBlank()) {
            revokedRefreshTokenIds.add(tokenId);
        }
    }

    public boolean isRevoked(String tokenId) {
        return tokenId != null && revokedRefreshTokenIds.contains(tokenId);
    }
}
