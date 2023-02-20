package pe.com.nttdbank.model;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountRestClient {

    public Integer accountId;
    public Integer clientId;
    public DebitCardRestClient debitCard;
    public BigDecimal amount;
    public Boolean isMain;
    public Boolean isActive;
    public Instant createdAt;
    public Instant updateddAt; 
}
