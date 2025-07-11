package com.itjamz.pond_back.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Builder
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    private String sabun;
    private String id;
    private String pw;
    private String name;

}
