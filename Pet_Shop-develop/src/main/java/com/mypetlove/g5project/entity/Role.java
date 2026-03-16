package com.mypetlove.g5project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
@Entity(name = "Roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private Integer roleId;


    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<AccountRole> role;
}
