package com.exchangediary.group.ui;

import com.exchangediary.group.service.GroupQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupQueryService groupQueryService;

    @GetMapping
    public String createOrJoinGroup() {
        return "group/group-create-join";
    }

    @GetMapping("/{groupId}")
    public String showCalendar(Model model, @PathVariable String groupId) {
        model.addAttribute("group", groupQueryService.getGroupMonthlyInfo(groupId));
        return "group/group-monthly";
    }
}
