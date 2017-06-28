package com.rock.coffeepickles.repository;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository("coffeeRepository")
public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
}
