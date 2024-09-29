package org.example.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/prepare_payment")
    public ResponseEntity<String> preparePayment(@RequestBody TransactionData transactionData){
        try {
            Payment payment = new Payment();
            payment.setOrderNumber(transactionData.getOrderNumber());
            payment.setItem(transactionData.getItem());
            payment.setPreparationStatus(PaymentStatus.PENDING.name());
            payment.setPrice(transactionData.getPrice());
            payment.setPaymentMode(transactionData.getPaymentMode());
            paymentRepository.save(payment);

            if(shouldFailedDuringPrepare()){
                throw new RuntimeException("Prepare phase failed for payment "+ transactionData.getOrderNumber());

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during payment preparation");
        }
        return  null;
    }

    private boolean shouldFailedDuringPrepare() {
        return true;
    }

    @PostMapping("/commit_payment")
    public ResponseEntity<String> commitPayment(@RequestBody TransactionData transactionData){
        Payment payment = paymentRepository.findByItem(transactionData.getItem());

        if(payment != null && payment.getPreparationStatus().equalsIgnoreCase(PaymentStatus.PENDING.name())){
            payment.setPreparationStatus(PaymentStatus.APPROVED.name());
            paymentRepository.save(payment);

            return ResponseEntity.ok("Payment Committed successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment cannot be committed");
    }

    @PostMapping("/rollback_payment")
    public ResponseEntity<String> rollbackPayment(@RequestBody TransactionData transactionData){
        Payment payment = paymentRepository.findByItem(transactionData.getItem());

        if(payment != null){
            payment.setPreparationStatus(PaymentStatus.ROLLBACK.name());
            paymentRepository.save(payment);

            return ResponseEntity.ok("Payment rolled back successfully");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during payment rollback");
    }
}
