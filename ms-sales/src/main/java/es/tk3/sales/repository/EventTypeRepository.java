package es.tk3.sales.repository;

import es.tk3.sales.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {}
