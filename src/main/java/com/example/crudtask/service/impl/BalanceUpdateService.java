package com.example.crudtask.service.impl;

import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BalanceUpdateService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AccountDAO accountDAO;


    @Scheduled(fixedRate = 30000)
    @Transactional
    public void updateBalances() {
        List<User> users = userDAO.findAll();

        for (User user : users) {
            Account account = user.getAccount();
            if (account != null && account.getInitialBalance() != null) {
                BigDecimal currentBalance = account.getBalance();
                BigDecimal initialBalance = account.getInitialBalance();

                BigDecimal tenPercentIncrease = new BigDecimal("1.10");

                BigDecimal maxBalance = initialBalance.multiply(new BigDecimal("2.07"));

                BigDecimal newBalance = currentBalance.multiply(tenPercentIncrease);

                if (newBalance.compareTo(maxBalance) > 0) {
                    newBalance = maxBalance;
                }

                account.setBalance(newBalance.setScale(2, RoundingMode.HALF_UP));

                accountDAO.save(account);
            }
        }
    }
}
