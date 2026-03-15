package com.mypetlove.g5project.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleId implements Serializable {

    private Integer accountId;
    private Integer roleId;
}
