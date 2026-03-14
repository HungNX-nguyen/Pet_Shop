package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
@Entity
@Builder
@Table(name = "Pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "petId")
    private Integer petId;

    private String name;
    private String type;
    private String breed;
    private String gender;

    private Float weight;
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Account owner;
}
