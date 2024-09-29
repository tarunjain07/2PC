package org.example.payment.controller;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String orderNumber;
    private String item;
    private String preparationStatus;
    private String paymentMode;
    private String price;
}
