package com.fse.equipment_management.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class AppUser {

    @Id
    private String id;

    @Indexed(unique = true)
    private String registerNo;

    private String username;
    private String password;
    private UserRole role;
    private boolean active;
}
