package es.tk3.operations.repository;

import es.tk3.operations.model.MenuTemplateRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuTemplateRepository extends JpaRepository<MenuTemplateRef, Long> {
}
