package pe.com.nttdbank.model;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "user")
public class User {

    public ObjectId userId;
    public String debitCardNumber;
    public String debitCardDueDate;
    public String debitCardValidationCode;
    public String identificationDocumentType;
    public String identificationDocumentNumber;
    public Integer password;
    public String debitCardPin;
}
