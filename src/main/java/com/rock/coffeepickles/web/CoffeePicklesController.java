package com.rock.coffeepickles.web;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.domain.Payment;
import com.rock.coffeepickles.service.CoffeeService;
import com.rock.coffeepickles.service.CustomerService;
import com.rock.coffeepickles.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.security.Principal;

@Controller
public class CoffeePicklesController {

    private final CoffeePriceService coffeePriceService;
    @Autowired
    CoffeeService coffeeService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PaymentService paymentService;

    @Autowired
    public CoffeePicklesController(CoffeePriceService coffeePriceService) {
        this.coffeePriceService = coffeePriceService;
    }

    @RequestMapping(value="/")
    @PreAuthorize("hasAuthority('ROLE_SSL_USER')")
    public String home(Model model, Principal principal, Payment payment) {
        Customer user = (Customer) ((Authentication) principal).getPrincipal();
        model.addAttribute("user",user);
        return "home";
    }

    @RequestMapping(value="/coffee", method= RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SSL_USER')")
    public String Coffee(Principal principal) {
        Customer user = (Customer) ((Authentication) principal).getPrincipal();
        Coffee coffee = coffeePriceService.getCoffeePrice(user);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        coffee.setTime(timestamp);
        user.purchaseCoffee(coffee);
        customerService.updateUser(user);
        coffeeService.addCoffee(coffee);
        return "redirect:/";
    }

    @RequestMapping(value="/payment", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SSL_USER')")
    public String Payment(Payment payment, Principal principal) {
        Customer user = (Customer) ((Authentication) principal).getPrincipal();
        //The payment object amount is returned from the form; now set the date
        //and the user
        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        payment.setDate(timestamp);
        payment.setUser(user);
        user.processPayment(payment);
        paymentService.addPayment(payment);
        customerService.updateUser(user);
        return "redirect:/";
    }


}
