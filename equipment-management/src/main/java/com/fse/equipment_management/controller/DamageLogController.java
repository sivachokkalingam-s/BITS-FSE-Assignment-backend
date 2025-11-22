package com.fse.equipment_management.controller;

import com.fse.equipment_management.data.DamageLog;
import com.fse.equipment_management.data.Equipment;
import com.fse.equipment_management.repository.DamageLogRepository;
import com.fse.equipment_management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/damage")
@RequiredArgsConstructor
public class DamageLogController {

    private final DamageLogRepository damageLogRepository;
    private final EquipmentRepository equipmentRepository;

    @PostMapping("/report")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> reportDamage(@RequestBody DamageLog damageLog) {
        Optional<Equipment> eq = equipmentRepository.findById(damageLog.getEquipmentId());
        if (eq.isEmpty()) {
            return ResponseEntity.badRequest().body("Equipment not found");
        }

        damageLog.setStatus("REPORTED");
        damageLog.setReportedDate(LocalDateTime.now());
        damageLogRepository.save(damageLog);

        return ResponseEntity.ok("Damage reported successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public List<DamageLog> getAllLogs() {
        return damageLogRepository.findAll();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public List<DamageLog> getMyReports(@RequestParam String username) {
        return damageLogRepository.findByReportedBy(username);
    }

    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam String status) {
        Optional<DamageLog> damageOpt = damageLogRepository.findById(id);
        if (damageOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Damage log not found");
        }

        DamageLog log = damageOpt.get();
        log.setStatus(status);

        if ("FIXED".equalsIgnoreCase(status)) {
            log.setResolvedDate(LocalDateTime.now());
        }

        damageLogRepository.save(log);
        return ResponseEntity.ok("Damage status updated successfully");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public List<DamageLog> getPendingLogs() {
        return damageLogRepository.findByStatus("REPORTED");
    }
}
