package com.rock.coffeepickles.repository;

import com.rock.coffeepickles.domain.Coffee;
import com.rock.coffeepickles.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sboulden on 6/27/17.
 */
@Repository("coffeeRepository")
public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
}
