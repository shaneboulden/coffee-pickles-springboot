package com.rock.coffeepickles.service;

import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("customerService")
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Transactional
    public Customer getUser(long id) {
        return customerRepository.findOne(id);
    }

    @Transactional
    public Customer getUser(String userName) {
        return customerRepository.findByUserName(userName);
    }

    @Transactional
    public void addUser(Customer user) {
        customerRepository.save(user);
    }

    @Transactional
    public void updateUser(Customer user) {
        customerRepository.save(user);

    }

    @Transactional
    public void deleteUser(Customer user) {
        customerRepository.save(user);
    }
}
