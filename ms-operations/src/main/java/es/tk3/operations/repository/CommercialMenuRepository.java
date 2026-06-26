package es.tk3.operations.repository;

import es.tk3.operations.model.CommercialMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommercialMenuRepository extends JpaRepository<CommercialMenu, UUID> {
}
