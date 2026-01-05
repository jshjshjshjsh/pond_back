package com.itjamz.pond_back.calendar.domain.entity;

import com.itjamz.pond_back.user.domain.entity.Member;

public interface Work {
    Member member = null;

    public Member getMember();
}
