package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record GroupDailyProgressDto(
        String groupId,
        String groupName,
        LocalDate date,
        int totalMembers,
        int membersWhoLogged,
        boolean completionMet,
        List<MemberProgressDto> members
) {}