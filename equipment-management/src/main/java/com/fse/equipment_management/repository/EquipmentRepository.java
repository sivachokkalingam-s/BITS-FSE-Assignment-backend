package com.fse.equipment_management.repository;

import com.fse.equipment_management.data.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, String> {

    List<Equipment> findByCategoryIgnoreCase(String category);

    List<Equipment> findByAvailableGreaterThan(int count);
}
