package pe.com.nttdbank.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class CreditRestClient {

    public Integer creditId;
    public Integer clientId;
    public LocalDate startDate;
    public Integer loan;
    public Integer monthlyPaymentDead;
    public BigDecimal initialBalance;
    public BigDecimal currentBalance;
    public Boolean isActive;
    public Instant createdAt;
    public Instant updateddAt;
}
