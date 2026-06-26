package es.tk3.kitchen.repository;

import es.tk3.kitchen.model.MenuTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuTemplateRepository extends JpaRepository<MenuTemplate, Long > {}
