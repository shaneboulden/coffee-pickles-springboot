package com.rock.coffeepickles.service;

import com.rock.coffeepickles.domain.Payment;
import com.rock.coffeepickles.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("paymentService")
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    public Payment getPayment(Long id) {
        return paymentRepository.findOne(id);
    }

    @Transactional
    public void addPayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional
    public void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }
}
