package com.example.speendlyclaude.config;

import com.example.speendlyclaude.model.Expenses;
import com.example.speendlyclaude.model.User;
import com.example.speendlyclaude.repository.ExpenseRepository;
import com.example.speendlyclaude.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_EMAIL = "demo@spendly.com";

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail(DEMO_EMAIL).isPresent()) {
            return;
        }

        User demoUser = new User();
        demoUser.setName("Demo User");
        demoUser.setEmail(DEMO_EMAIL);
        demoUser.setPasswordHash(passwordEncoder.encode("demo123"));
        userRepository.save(demoUser);

        List<Expenses> expenses = List.of(
                expense(demoUser, 2, "Food", "450.00", "Lunch with team"),
                expense(demoUser, 4, "Transport", "120.00", "Metro card top-up"),
                expense(demoUser, 6, "Bills", "1850.50", "Electricity bill"),
                expense(demoUser, 10, "Health", "600.00", "Pharmacy"),
                expense(demoUser, 14, "Entertainment", "499.00", "Movie tickets"),
                expense(demoUser, 18, "Shopping", "1299.00", "Running shoes"),
                expense(demoUser, 22, "Other", "250.00", "Birthday gift"),
                expense(demoUser, 26, "Food", "320.75", "Weekly groceries")
        );
        expenseRepository.saveAll(expenses);
    }

    private Expenses expense(User user, int dayOfMonth, String category, String amount, String description) {
        Expenses expense = new Expenses();
        expense.setUser(user);
        expense.setAmount(new BigDecimal(amount));
        expense.setCategory(category);
        expense.setDate(YearMonth.now().atDay(dayOfMonth));
        expense.setDescription(description);
        return expense;
    }
}
