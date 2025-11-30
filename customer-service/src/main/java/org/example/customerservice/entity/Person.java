package org.example.customerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("persons")
public class Person {
    
    @Id
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("gender")
    private String gender;
    
    @Column("identification")
    private String identification;
    
    @Column("address")
    private String address;
    
    @Column("phone")
    private String phone;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}

