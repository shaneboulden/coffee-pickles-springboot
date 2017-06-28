package com.rock.coffeepickles.repository;

import com.rock.coffeepickles.domain.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository("customerRepository")
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Customer findByUserName(String userName);
}
