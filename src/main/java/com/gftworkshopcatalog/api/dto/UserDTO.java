package com.gftworkshopcatalog.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class UserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;

}
