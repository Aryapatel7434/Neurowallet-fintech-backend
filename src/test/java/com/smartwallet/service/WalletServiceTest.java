package com.smartwallet.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WalletServiceTest {

    @BeforeEach
    void setup() {

        System.out.println("Test Started");
    }

    @AfterEach
    void cleanup() {

        System.out.println("Test Finished");
    }

    @Test
    void shouldAddTwoNumbers() {

        int result = 10 + 20;

        assertEquals(30, result);
    }

    @Test
    void amountShouldBePositive() {

        int amount = 500;

        assertTrue(amount > 0);
    }

    @Test
    void amountShouldNotBeNegative() {

        int amount = -100;

        assertFalse(amount > 0);
    }

    @Test
    void objectShouldNotBeNull() {

        String email = "arya@gmail.com";

        assertNotNull(email);
    }
    @Test
    void withdrawShouldReduceBalance(){
        int balance = 1000;
        
        int withdrawAmount = 300;
        
        int remainingBalance = balance - withdrawAmount;
        
        assertEquals(700,remainingBalance);
    }
    
   @Test
void depositShouldIncreaseBalance() {

    int balance = 1000;

    int depositAmount = 500;

    int updatedBalance =
            balance + depositAmount;

    assertEquals(
            1500,
            updatedBalance
    );
}
}