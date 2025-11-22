package com.fse.equipment_management.controller;

import com.fse.equipment_management.data.BorrowRecord;
import com.fse.equipment_management.data.Equipment;
import com.fse.equipment_management.repository.BorrowRepository;
import com.fse.equipment_management.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowRepository borrowRepository;
    private final EquipmentRepository equipmentRepository;

    @PostMapping("/request")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> requestBorrow(@RequestBody BorrowRecord request) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(request.getEquipmentId());
        if (equipmentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Equipment not found");
        }

        Equipment equipment = equipmentOpt.get();
        if (equipment.getAvailable() <= 0) {
            return ResponseEntity.badRequest().body("Equipment not available");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        request.setBorrower(username);
        request.setEquipmentName(equipment.getName());
        request.setStatus("PENDING");
        request.setRequestDate(LocalDateTime.now());
        borrowRepository.save(request);

        return ResponseEntity.ok("Borrow request submitted successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public List<BorrowRecord> getAllBorrowRequests() {
        return borrowRepository.findAll();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public List<BorrowRecord> getMyBorrowRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return borrowRepository.findByBorrower(username);
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable String id) {
        Optional<BorrowRecord> borrowOpt = borrowRepository.findById(id);
        if (borrowOpt.isEmpty()) return ResponseEntity.badRequest().body("Request not found");

        BorrowRecord borrow = borrowOpt.get();
        if (!borrow.getStatus().equals("PENDING"))
            return ResponseEntity.badRequest().body("Already processed");

        Optional<Equipment> equipmentOpt = equipmentRepository.findById(borrow.getEquipmentId());
        if (equipmentOpt.isEmpty()) return ResponseEntity.badRequest().body("Equipment not found");

        Equipment equipment = equipmentOpt.get();
        if (equipment.getAvailable() <= 0)
            return ResponseEntity.badRequest().body("Equipment not available");

        equipment.setAvailable(equipment.getAvailable() - 1);
        equipmentRepository.save(equipment);

        borrow.setStatus("APPROVED");
        borrow.setApproveDate(LocalDateTime.now());
        borrowRepository.save(borrow);

        return ResponseEntity.ok("Request approved successfully");
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> rejectRequest(@PathVariable String id) {
        Optional<BorrowRecord> borrowOpt = borrowRepository.findById(id);
        if (borrowOpt.isEmpty()) return ResponseEntity.badRequest().body("Request not found");

        BorrowRecord borrow = borrowOpt.get();
        if (!borrow.getStatus().equals("PENDING"))
            return ResponseEntity.badRequest().body("Already processed");

        borrow.setStatus("REJECTED");
        borrowRepository.save(borrow);

        return ResponseEntity.ok("Request rejected");
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> returnEquipment(@PathVariable String id) {
        Optional<BorrowRecord> borrowOpt = borrowRepository.findById(id);
        if (borrowOpt.isEmpty()) return ResponseEntity.badRequest().body("Request not found");

        BorrowRecord borrow = borrowOpt.get();
        if (!borrow.getStatus().equals("APPROVED"))
            return ResponseEntity.badRequest().body("Request not approved or already returned");


        Optional<Equipment> equipmentOpt = equipmentRepository.findById(borrow.getEquipmentId());
        if (equipmentOpt.isPresent()) {
            Equipment equipment = equipmentOpt.get();
            equipment.setAvailable(equipment.getAvailable() + 1);
            equipmentRepository.save(equipment);
        }

        borrow.setStatus("RETURNED");
        borrow.setReturnDate(LocalDateTime.now());
        borrowRepository.save(borrow);

        return ResponseEntity.ok("Equipment returned successfully");
    }
}
