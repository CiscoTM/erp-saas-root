package es.tk3.common.repository;

import es.tk3.common.model.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    Optional<TenantUser> findByUsername(String username);
}