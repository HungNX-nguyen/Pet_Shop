package com.mypetlove.g5project.controller;

import com.mypetlove.g5project.dto.ServiceRequest;
import com.mypetlove.g5project.entity.Service;
import com.mypetlove.g5project.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "All") String category,
                       @RequestParam(defaultValue = "newest") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {

        Page<Service> servicePage = serviceManagementService.getPage(keyword, category, sort, page, size);

        model.addAttribute("servicePage", servicePage);
        model.addAttribute("services", servicePage.getContent());

        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);

        model.addAttribute("totalServices", serviceManagementService.countAll());
        model.addAttribute("activeServices", serviceManagementService.countActive());
        model.addAttribute("inactiveServices", serviceManagementService.countInactive());
        model.addAttribute("averagePrice", serviceManagementService.averagePrice());

        return "admin/services/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("service", new ServiceRequest());
        return "admin/services/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("service") ServiceRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "admin/services/create";
        }

        String username = authentication != null ? authentication.getName() : null;
        serviceManagementService.create(request, username);

        redirectAttributes.addFlashAttribute("successMessage", "Add service successfully");
        return "redirect:/admin/services";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Service serviceData = serviceManagementService.getById(id);

        ServiceRequest request = new ServiceRequest();
        request.setName(serviceData.getName());
        request.setCategory(serviceData.getCategory());
        request.setDescription(serviceData.getDescription());
        request.setPrice(serviceData.getPrice());
        request.setDuration(serviceData.getDuration());

        model.addAttribute("serviceId", id);
        model.addAttribute("service", request);

        return "admin/services/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("service") ServiceRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("serviceId", id);
            return "admin/services/edit";
        }

        serviceManagementService.update(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Update service successfully");
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        serviceManagementService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Change service status successfully");
        return "redirect:/admin/services";
    }
}