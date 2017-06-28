package com.rock.coffeepickles.repository;

import com.rock.coffeepickles.domain.Payment;
import org.springframework.data.repository.CrudRepository;


public interface PaymentRepository extends CrudRepository<Payment,Long> {
}
