package es.tk3.sales.controller;

import es.tk3.sales.model.Room;
import es.tk3.sales.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales/rooms")
public class RoomController {
    @Autowired private RoomRepository roomRepository;

    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @Transactional
    public ResponseEntity<Room> createRoom(@RequestBody Room room){
        Room savedRoom = roomRepository.save(room);
        return ResponseEntity.ok(savedRoom);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms(){
        return ResponseEntity.ok(roomRepository.findAll());
    }
}
