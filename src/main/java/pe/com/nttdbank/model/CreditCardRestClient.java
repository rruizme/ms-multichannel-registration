package pe.com.nttdbank.model;

import java.math.BigDecimal;
import java.time.Instant;

public class CreditCardRestClient {

    public Integer creditCardId;
    public Integer clientId;
    public String cardNumber;
    public String pin;
    public String expirationDate;
    public String cardValidationCode;
    public Integer monthlyCutoffDate;
    public Integer monthlyPaymentDeadline;
    public BigDecimal currentBalance;
    public BigDecimal creditLimit;
    public Boolean isActive;
    public Instant createdAt;
    public Instant updateddAt;
}
