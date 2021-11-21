package com.itrex.java.lab.projectcrmspringboot.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * The role class is responsible for the user's role.
 * role's name is only roleName.toUpperCase();
 * Default role = Role {id = 2, roleName = "User"}
 * If the this.role is removed, this.role is changed to default role in all users
 */

@Entity
@Table(name = "role", schema = "public")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    @Column(name = "role_name")
    private String roleName;

    @Setter
    @OneToMany(mappedBy = "role")
    private List<User> users;

    public Integer getId() {
        return id;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName.toUpperCase();
    }

    @Override
    public String toString() {
        return "\nRole{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                '}';
    }

}

