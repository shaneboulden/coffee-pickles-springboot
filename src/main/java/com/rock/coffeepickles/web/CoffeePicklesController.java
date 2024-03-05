package com.rock.coffeepickles.web;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.domain.Payment;
import com.rock.coffeepickles.service.CoffeePriceService;
import com.rock.coffeepickles.service.CoffeeService;
import com.rock.coffeepickles.service.CustomerService;
import com.rock.coffeepickles.service.PaymentService;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class CoffeePicklesController {

    private final CoffeePriceService coffeePriceService;
    private KieContainer container;
    @Autowired
    CoffeeService coffeeService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PaymentService paymentService;

    @Autowired
    public CoffeePicklesController(CoffeePriceService coffeePriceService) {
        this.coffeePriceService = coffeePriceService;
        KieServices services = KieServices.Factory.get();
        container = services.newKieClasspathContainer();
    }

    @RequestMapping(value="/")
    public String home(Model model) {
        Customer user = customerService.getUser("user1");
        if (user == null) {
            Customer customer_zero = new Customer("user1");
            customerService.addUser(customer_zero);
            user = customerService.getUser("user1");
        }
        model.addAttribute("user",user);
        model.addAttribute("payment", new Payment());
        return "home";
    }

    @RequestMapping(value="/coffee", method= RequestMethod.POST)
    public String Coffee( RedirectAttributes redirAttrs) {
        Customer user = customerService.getUser("user1");
        Coffee coffee = coffeePriceService.getCoffeePrice(user, container);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        coffee.setTime(timestamp);
        user.purchaseCoffee(coffee);
        customerService.updateUser(user);
        coffeeService.addCoffee(coffee);
        if (coffee.getPrice().compareTo(new BigDecimal("0.00")) == 0) {
            redirAttrs.addFlashAttribute("message", "You've hit your fifth coffee, it's on us!");
        }
        else{
            redirAttrs.addFlashAttribute("message", "Coffee purchased!");
        }
        return "redirect:/";
    }

    @RequestMapping(value="/payment", method = RequestMethod.POST)
    public String Payment(Payment payment, RedirectAttributes redirAttrs) {
        Customer user = customerService.getUser("user1");
        //The payment object amount is returned from the form; now set the date
        //and the user
        java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        payment.setDate(timestamp);
        payment.setUser(user);
        user.processPayment(payment);
        paymentService.addPayment(payment);
        customerService.updateUser(user);
        redirAttrs.addFlashAttribute("message", "Payment of $"+payment.getAmount()+" submitted successfully");
        return "redirect:/";
    }
}
