package com.rock.coffeepickles.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
public class Coffee {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER)
    @JsonBackReference
    private Customer user;
    private BigDecimal price;
    private Timestamp date;

    //JPA requires that a default constructor exists
    //for entities
    protected Coffee() {}

    public Coffee(BigDecimal price, Customer user) {
        this.price = price;
        this.user = user;
    }

    public void setPrice(BigDecimal price) { this.price = price; }

    public Timestamp getDate() {
        return this.date;
    }

    public void setTime(Timestamp date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Customer getUser() {
        return this.user;
    }
}
