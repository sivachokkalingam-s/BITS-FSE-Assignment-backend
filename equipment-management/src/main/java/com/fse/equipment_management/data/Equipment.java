package com.fse.equipment_management.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "equipment")
public class Equipment {

    @Id
    private String id;

    private String name;
    private String category;
    private String condition;

    private int quantity;
    private int available;
}
