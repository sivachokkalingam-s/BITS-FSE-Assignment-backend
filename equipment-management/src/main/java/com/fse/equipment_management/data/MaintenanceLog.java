package com.fse.equipment_management.data;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceLog {

    private String id;
    private LocalDateTime date;
    private String description;
    private String performedBy;
    private String remarks;
}
