package es.tk3.operations.repository;

import es.tk3.operations.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID> {
}
