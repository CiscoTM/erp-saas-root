package es.tk3.sales.repository;

import es.tk3.sales.model.OperationalParameterRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationalParameterRefRepository extends JpaRepository<OperationalParameterRef, String> {
}
