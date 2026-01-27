package com.slice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {


    @GetMapping("/staff/dashboard")
    public String staffDashboard() {
        return "staff-dashboard";
    }

}
