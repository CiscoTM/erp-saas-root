package es.tk3.sales.repository;

import es.tk3.sales.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStartDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND (:start < b.endDate AND :end > b.startDate) " +
            "AND b.status != 'CANCELLED'")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}

