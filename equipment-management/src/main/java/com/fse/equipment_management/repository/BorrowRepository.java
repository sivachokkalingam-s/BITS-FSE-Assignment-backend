package com.fse.equipment_management.repository;

import com.fse.equipment_management.data.BorrowRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BorrowRepository extends MongoRepository<BorrowRecord, String> {
    List<BorrowRecord> findByBorrower(String borrower);
}
