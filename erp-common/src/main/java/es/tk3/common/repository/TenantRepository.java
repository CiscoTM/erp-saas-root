package es.tk3.common.repository;

import es.tk3.common.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query(value = "SELECT * FROM tenants WHERE id = :id", nativeQuery = true)
    Optional<Tenant> findByIdForRouting(@Param("id") String id);
}
