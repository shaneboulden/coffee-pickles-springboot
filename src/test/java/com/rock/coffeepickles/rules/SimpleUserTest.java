package com.rock.coffeepickles.rules;

import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.service.CoffeePriceService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.rock.coffeepickles.domain.Coffee;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import java.math.BigDecimal;


public class SimpleUserTest {

    KieContainer container;

    @Before
    public void setup() {
        KieServices services = KieServices.Factory.get();
        container = services.newKieClasspathContainer();
    }

    @Test
    public void everyFifthCoffeeIsFree() {
        CoffeePriceService coffeePriceService = new CoffeePriceService();
        Customer user = new Customer("user1");
        // Create 4 coffees for the user
        for (int i=0; i < 4; i++) {
            user.purchaseCoffee(new Coffee(new BigDecimal("1.00"), user));
        }

        Coffee coffee = coffeePriceService.getCoffeePrice(user,container);

        // After the rule fires, the coffee should be free
        assertThat(coffee.getPrice(), Matchers.comparesEqualTo(new BigDecimal("0.00")));
    }

    @Test
    public void coffeeIsNormallyNotFree() {
        CoffeePriceService coffeePriceService = new CoffeePriceService();
        Customer user = new Customer("user1");

        Coffee coffee = coffeePriceService.getCoffeePrice(user, container);

        // The coffee should not be free
        assertThat(coffee.getPrice(), not(0.00));
    }

    @After
    public void tearDown() {
        container.dispose();
    }
}
