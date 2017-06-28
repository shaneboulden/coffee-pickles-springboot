package com.rock.coffeepickles.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.SortNatural;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
public class Customer implements UserDetails{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String userName;
    private BigDecimal balance;

    protected Customer() {}

    @OneToMany(mappedBy="user", fetch=FetchType.EAGER)
    @JsonManagedReference
    private Set<Coffee> coffees = new HashSet<>();

    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    @SortNatural
    @OrderBy("date ASC")
    private SortedSet<Payment> payments = new TreeSet<>();

    public Customer(String userName) {
        this.userName = userName;
        this.balance = new BigDecimal("0.00");
    }

    public void purchaseCoffee(Coffee coffee) {
        this.coffees.add(coffee);
        balance = this.getBalance().add(coffee.getPrice());
        this.setBalance(balance);
    }

    public void processPayment(Payment payment) {
       this.payments.add(payment);
       balance = this.getBalance().subtract(payment.getAmount());
       this.setBalance(balance);
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public String getUserName() {
        return userName;
    }

    public Set<Coffee> getCoffees() {
        return coffees;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getNumCoffees() {
        return this.coffees.size();
    }

    public Set<Payment> getRecentPayments() {
        TreeSet<Payment> recent = new TreeSet<>();
        Iterator<Payment> itr = payments.iterator();
        int numRecent = payments.size() < 5 ? payments.size() : 5;
        for (int i=0; i< numRecent; i++) {
            recent.add(itr.next());
        }
        return recent;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, userName='%s']",
                id, userName);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SSL_USER");
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}