package com.rock.coffeepickles.repository;

import com.rock.coffeepickles.domain.Payment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by sboulden on 6/28/17.
 */
public interface PaymentRepository extends CrudRepository<Payment,Long> {
}
