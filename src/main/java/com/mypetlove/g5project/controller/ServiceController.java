package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.ServiceDto.ServiceDto;
import com.mypetlove.g5project.service.PetServiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/services")
public class ServiceController {

    private final PetServiceService service;

    // GET /services
    @GetMapping
    public String getAllServices(Model model) {

        model.addAttribute("services",
                service.getAllActiveServices());

        return "service-booking/servicelisting";
    }

    // GET /services/{id}
    @GetMapping("/{id}")
    public String getServiceDetail(@PathVariable Integer id,
                                   Model model) {

        model.addAttribute("service",
                service.getServiceById(id));

        return "service-booking/servicedetail";
    }
}