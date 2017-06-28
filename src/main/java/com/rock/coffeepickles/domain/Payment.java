package com.rock.coffeepickles.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
public class Payment implements Comparable<Payment>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Customer user;
    private BigDecimal amount;
    private Timestamp date;

    protected Payment() {}

    public Payment(BigDecimal amount, Customer user, Timestamp date) {
        this.amount = amount;
        this.user = user;
        this.date = date;
    }

    public Customer getUser() {
        return this.user;
    }

    public Timestamp getDate() {
        return this.date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setUser(Customer user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("" +
                "Payment object: \nId: %d\nAmount: %d\nUser: %s",
                this.id,this.amount, this.user.getUserName());
    }

    @Override
    public int compareTo(Payment payment) {
        return payment.getDate().compareTo(this.getDate());
    }
}
