package pe.com.nttdbank.model;

// import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "usuario")
public class User extends PanacheMongoEntityBase{

    @BsonProperty("id")
    //@BsonId
    public Integer userId;
    public String debitCardNumber;
    public String debitCardDueDate;
    public String debitCardValidationCode;
    public Character identificationDocumentType;
    public String identificationDocumentNumber;

    public User() {

    }

    public User(Integer userId, String debitCardNumber, String debitCardDueDate, String debitCardValidationCode, Character identificationDocumentType, String identificationDocumentNumber) {
        this.userId = userId;
        this.debitCardNumber = debitCardNumber;
        this.debitCardDueDate = debitCardDueDate;
        this.debitCardValidationCode = debitCardValidationCode;
        this.identificationDocumentType = identificationDocumentType;
        this.identificationDocumentNumber = identificationDocumentNumber;
    }
}
