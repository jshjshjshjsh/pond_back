package com.itjamz.pond_back.security.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class AuthenticationRequest {
    private String id;
    private String pw;
}