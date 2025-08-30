package com.teachtouch.backend.auth.repository;

import com.teachtouch.backend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {
}