package com.poja.app.endpoint.rest.controller.health;

import com.poja.app.model.Donation;
import com.poja.app.model.EntryDTO;
import com.poja.app.model.Help;
import com.poja.app.repository.DonationRepository;
import com.poja.app.repository.HelpRepository;
import com.poja.app.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
public class TsinjoController {

    private final DonationService donationService;
    private final DonationRepository donationRepository;
    private final HelpRepository helpRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<EntryDTO> entries = new ArrayList<>();

        // Donations
        List<Donation> donations = donationRepository.findAllByOrderByCreatedAtDesc();
        entries.addAll(donations.stream().map(this::toEntryDTOFromDonation).collect(Collectors.toList()));

        // Helps
        List<Help> helps = helpRepository.findAllByOrderByCreatedAtDesc();
        entries.addAll(helps.stream().map(this::toEntryDTOFromHelp).collect(Collectors.toList()));

        // Tri anti-chronologique
        entries = entries.stream()
                .sorted(Comparator.comparing(EntryDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());

        model.addAttribute("entries", entries);
        return "index";
    }

    @PostMapping("/donate")
    public String donate(
            @RequestParam("email") String email,
            @RequestParam("fullName") String fullName,
            @RequestParam("amount") Long amount,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "reference", required = false) String note
    ) {
        // La validation minimale est faite par annotations HTML (required). Ici on fait un peu de robustesse.
        if (email == null || email.isBlank() || fullName == null || fullName.isBlank() || amount == null || amount <= 0) {
            // On pourrait ajouter un attribut d'erreur au model, mais simple redirect pour l'examen.
            return "redirect:/";
        }

        donationService.submitDonation(email.trim(), fullName.trim(), amount, method, note);
        return "redirect:/";
    }

    /* ----- Mapping helpers ----- */

    private EntryDTO toEntryDTOFromDonation(Donation d) {
        String person = safeFullNameAndEmail(d.getDonor().getFullName(), d.getDonor().getEmail());
        return new EntryDTO(
                "DONATION",
                person,
                d.getPayment() != null ? d.getPayment().getAmount() : null,
                d.getPayment() != null ? d.getPayment().getState() : null,
                d.getCreatedAt() != null ? d.getCreatedAt() : Instant.EPOCH,
                d.getNote(),
                d.getPayment() != null ? d.getPayment().getMethod() : null
        );
    }

    private EntryDTO toEntryDTOFromHelp(Help h) {
        String person = safeFullNameAndEmail(h.getBeneficiary().getFullName(), h.getBeneficiary().getEmail());
        return new EntryDTO(
                "HELP",
                person,
                h.getPayment() != null ? h.getPayment().getAmount() : null,
                h.getPayment() != null ? h.getPayment().getState() : null,
                h.getCreatedAt() != null ? h.getCreatedAt() : Instant.EPOCH,
                h.getDescription(),
                h.getPayment() != null ? h.getPayment().getMethod() : null
        );
    }

    private String safeFullNameAndEmail(String fullName, String email) {
        String name = (fullName == null || fullName.isBlank()) ? "—" : fullName;
        String mail = (email == null || email.isBlank()) ? "" : " (" + email + ")";
        return name + mail;
    }
}