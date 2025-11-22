package com.fse.equipment_management.controller;

import com.fse.equipment_management.data.Equipment;
import com.fse.equipment_management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STUDENT', 'STAFF', 'ADMIN')")
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('STUDENT', 'STAFF', 'ADMIN')")
    public List<Equipment> getByCategory(@PathVariable String category) {
        return equipmentRepository.findByCategoryIgnoreCase(category);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('STUDENT', 'STAFF', 'ADMIN')")
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findByAvailableGreaterThan(0);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEquipment(@RequestBody Equipment equipment) {
        if (equipment.getAvailable() > equipment.getQuantity()) {
            return ResponseEntity.badRequest().body("Available cannot exceed total quantity");
        }

        equipmentRepository.save(equipment);
        return ResponseEntity.ok("Equipment added successfully");
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEquipment(@PathVariable String id, @RequestBody Equipment updated) {
        Optional<Equipment> eqOpt = equipmentRepository.findById(id);
        if (eqOpt.isEmpty()) return ResponseEntity.badRequest().body("Equipment not found");

        Equipment eq = eqOpt.get();
        eq.setName(updated.getName());
        eq.setCategory(updated.getCategory());
        eq.setCondition(updated.getCondition());
        eq.setQuantity(updated.getQuantity());
        eq.setAvailable(Math.min(updated.getAvailable(), updated.getQuantity()));

        equipmentRepository.save(eq);
        return ResponseEntity.ok("Equipment updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEquipment(@PathVariable String id) {
        if (!equipmentRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Equipment not found");
        }
        equipmentRepository.deleteById(id);
        return ResponseEntity.ok("Equipment deleted successfully");
    }

    @PutMapping("/mark-damaged/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> markAsDamaged(@PathVariable String id) {
        Optional<Equipment> eqOpt = equipmentRepository.findById(id);
        if (eqOpt.isEmpty()) return ResponseEntity.badRequest().body("Equipment not found");

        Equipment eq = eqOpt.get();
        if (eq.getAvailable() > 0) {
            eq.setAvailable(eq.getAvailable() - 1);
            equipmentRepository.save(eq);
            return ResponseEntity.ok("One unit marked as damaged");
        } else {
            return ResponseEntity.badRequest().body("No available units to mark as damaged");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'STAFF', 'ADMIN')")
    public ResponseEntity<?> getEquipmentById(@PathVariable String id) {
        return equipmentRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Equipment not found"));
    }

}
