package com.fse.equipment_management.repository;

import com.fse.equipment_management.data.DamageLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageLogRepository extends MongoRepository<DamageLog, String> {
    List<DamageLog> findByEquipmentId(String equipmentId);
    List<DamageLog> findByReportedBy(String reportedBy);
    List<DamageLog> findByStatus(String status);
}
