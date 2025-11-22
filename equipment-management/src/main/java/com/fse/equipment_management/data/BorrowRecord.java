package com.fse.equipment_management.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "borrow_records")
public class BorrowRecord {
    @Id
    private String id;
    private String equipmentId;
    private String equipmentName;
    private String borrower;
    private String status;
    private LocalDateTime requestDate = LocalDateTime.now();
    private LocalDateTime approveDate;
    private LocalDateTime returnDate;
}
