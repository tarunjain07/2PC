1. Start all 3 services
2. http://localhost:8081/h2-console/login.do (  jdbc:h2:mem:ordersdb)
3. http://localhost:8082/h2-console/login.do (  jdbc:h2:mem:paymentsdb)
4. localhost:8080/initiate_2pc

   Request body :
   
   {
    "orderNumber": "121141115",
    "item": "Jug",
    "price": "100101",
    "paymentMode": "online"
  }

5. return true from "shouldFailedDuringPrepare" to fail prepare phase and try rollback 
