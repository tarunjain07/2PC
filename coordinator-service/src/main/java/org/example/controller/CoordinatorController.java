package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CoordinatorController {
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/initiate_2pc")
    public String initiateTwoPhaseCommit(@RequestBody TransactionData transactionData){
        if(callPreparePhase(transactionData)){
            if(callCommitPhase(transactionData)){
                return "Transaction Commited Successfully";
            }else{
                callRollbackPhase(transactionData);
                return "Transaction Rollback";
            }
        }
        callRollbackPhase(transactionData);
        return "Transaction Rollback";
    }

    private void callRollbackPhase(TransactionData transactionData) {
        callServices("http://localhost:8081/rollback_order", transactionData);
        callServices("http://localhost:8082/rollback_payment", transactionData);
    }

    private boolean callCommitPhase(TransactionData transactionData) {
            boolean isOrderSuccess = callServices("http://localhost:8081/commit_order", transactionData);
            boolean isPaymentSuccess = callServices("http://localhost:8082/commit_payment", transactionData);
            return isOrderSuccess && isPaymentSuccess;
    }

    private boolean callPreparePhase(TransactionData transactionData){
        try {
            boolean isOrderSuccess = callServices("http://localhost:8081/prepare_order", transactionData);
            boolean isPaymentSuccess = callServices("http://localhost:8082/prepare_payment", transactionData);
            return isOrderSuccess && isPaymentSuccess;
        } catch (Exception e) {
            return false;
        }

    }

    private boolean callServices(String url, TransactionData transactionData){
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, transactionData, String.class);
        return stringResponseEntity.getStatusCode().is2xxSuccessful();

    }
}
