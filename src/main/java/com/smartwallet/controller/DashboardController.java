package com.smartwallet.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    @GetMapping("/stats")
    public Map<String, Object> getStats(){


        Map<String, Object> data = new HashMap<>();

        data.put("balance", 125000);
        data.put("income", 50000);
        data.put("expenses", 15000);
        data.put("savings", 35000);

        return data;
    }
}