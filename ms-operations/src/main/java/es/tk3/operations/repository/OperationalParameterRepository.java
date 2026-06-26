package es.tk3.operations.repository;

import es.tk3.operations.model.OperationalParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationalParameterRepository extends JpaRepository<OperationalParameter, Long> {

    @Query(value = "SELECT o FROM OperationalParameter o WHERE o.tenantId= :tenantId")
    Optional<OperationalParameter> findByTenantId(@Param("tenantId") String tenantId);

    OperationalParameter findFirstByTenantId(String tenantId);

}
