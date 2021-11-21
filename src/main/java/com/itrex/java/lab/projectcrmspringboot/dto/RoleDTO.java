package com.itrex.java.lab.projectcrmspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * The role class is responsible for the user's role.
 * role's name is only roleName.toUpperCase();
 * Default role = Role {id = 2, roleName = "User"}
 * If the this.role is removed, this.role is changed to default role in all users
 */

@AllArgsConstructor
@Data
@Builder
public class RoleDTO {

    private Integer id;
    private String roleName;

    @Override
    public String toString() {
        return "\nRoleDTO{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }

}

