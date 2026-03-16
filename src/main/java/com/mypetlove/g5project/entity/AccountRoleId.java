package com.mypetlove.g5project.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AccountRoleId implements Serializable {

    private Integer accountId;
    private Integer roleId;
}
