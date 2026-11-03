package com.example.speendlyclaude.controller;

import com.example.speendlyclaude.model.Expenses;
import com.example.speendlyclaude.model.User;
import com.example.speendlyclaude.repository.ExpenseRepository;
import com.example.speendlyclaude.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Authentication authentication, Model model) {
        User currentUser = currentUser(authentication);
        List<Expenses> expenses = expenseRepository.findByUserOrderByDateDesc(currentUser);
        BigDecimal total = expenses.stream()
                .map(Expenses::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        return "expenses";
    }

    @PostMapping
    public String add(@RequestParam BigDecimal amount,
                       @RequestParam String category,
                       @RequestParam LocalDate date,
                       @RequestParam(required = false) String description,
                       Authentication authentication) {
        Expenses expense = new Expenses();
        expense.setUser(currentUser(authentication));
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);
        expense.setDescription(description);
        expenseRepository.save(expense);
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        User currentUser = currentUser(authentication);
        expenseRepository.findById(id)
                .filter(expense -> expense.getUser().getId().equals(currentUser.getId()))
                .ifPresent(expenseRepository::delete);
        return "redirect:/expenses";
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}
