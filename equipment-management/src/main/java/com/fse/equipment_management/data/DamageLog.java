package com.fse.equipment_management.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "damage_logs")
public class DamageLog {

    @Id
    private String id;

    private String equipmentId;      // references Equipment._id
    private String reportedBy;       // username of student/staff who reported
    private String description;      // details about the damage
    private String status;           // e.g., "REPORTED", "UNDER_REPAIR", "FIXED"

    private LocalDateTime reportedDate;
    private LocalDateTime resolvedDate;
}
