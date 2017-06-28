package com.rock.coffeepickles.web;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

@RestController
public class CoffeePicklesDataController {

    @Autowired
    CustomerService customerService;

    @RequestMapping(value="/coffeedates/{username}",method= RequestMethod.GET)
    public HashMap<String,Integer> getCoffeesByDate(@PathVariable String username) {
        HashMap<String,Integer> map = new HashMap<>();
        Customer user = customerService.getUser(username);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Set<Coffee> coffeeSet = user.getCoffees();
        for (Coffee coffee : coffeeSet) {
            map.merge(format.format(coffee.getDate()), 1, Integer::sum);
        }
        return map;
    }

    @RequestMapping(value="/coffeedays/{username}",method=RequestMethod.GET)
    public LinkedHashMap<String,Integer> getCoffeesByDay(@PathVariable String username) {
        LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
        Customer user = customerService.getUser(username);
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        Set<Coffee> coffeeSet = user.getCoffees();
        String days[] = {"Sunday","Monday","Tuesday","Wednesday","Thursday",
                "Friday","Saturday"};
        for(String day : days)
            map.merge(day,0,Integer::sum);
        for(Coffee coffee : coffeeSet) {
            map.merge(format.format(coffee.getDate()),1,Integer::sum);
        }
        return map;
    }
}
