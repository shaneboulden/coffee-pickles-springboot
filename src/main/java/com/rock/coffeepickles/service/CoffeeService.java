package com.rock.coffeepickles.service;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("coffeeService")
public class CoffeeService {

    @Autowired
    CoffeeRepository coffeeRepository;

    @Transactional
    public Coffee getCoffee(long id) {
        return coffeeRepository.findOne(id);
    }

    @Transactional
    public void addCoffee(Coffee coffee) {
        coffeeRepository.save(coffee);
    }

    @Transactional
    public void updateCoffee(Coffee coffee) {
        coffeeRepository.save(coffee);

    }

    @Transactional
    public void deleteCoffee(Coffee coffee) {
        coffeeRepository.save(coffee);
    }
}

