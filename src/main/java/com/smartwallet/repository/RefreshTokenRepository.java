
package com.smartwallet.repository;

import com.smartwallet.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token")
    int deleteByToken(@Param("token") String token);
}
