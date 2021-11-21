package com.itrex.java.lab.projectcrmspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class PasswordDTOForChanges {

    private String oldPassword;
    private String newPassword;

}
