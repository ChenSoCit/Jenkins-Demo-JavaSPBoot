package com.java.TrainningJV.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCountResponse {
    private Long roleId;
    private String roleName;
    private Long userCount;
}
