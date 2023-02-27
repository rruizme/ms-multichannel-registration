package pe.com.nttdbank.model;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(database = "digital_wallet_system", collection = "user")
public class User {

    public ObjectId userId;
    public Integer kindOfPerson;
    public String identificationDocumentType;
    public String identificationDocumentNumber;
    public String name;
    public String email;
    public String phone;
    public Integer password;
}
