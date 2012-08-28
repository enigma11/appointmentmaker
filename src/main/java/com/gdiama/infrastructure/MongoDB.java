package com.gdiama.infrastructure;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

public class MongoDB {
    private static final String MONGOLAB_URI = "MONGOLAB_URI";
    private static volatile MongoDB mongoDB;
    private final MongoTemplate mongoTemplate;

    public static MongoDB get() throws UnknownHostException {
        if (mongoDB != null) {
            return mongoDB;
        }

        mongoDB = new MongoDB();
        return mongoDB;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    private MongoDB() throws UnknownHostException {
        mongoTemplate = initMongoTemplate();
    }

    private MongoTemplate initMongoTemplate() throws UnknownHostException {
        MongoURI mongoURI = new MongoURI(System.getenv(MONGOLAB_URI));
        return new MongoTemplate(new Mongo(mongoURI), mongoURI.getDatabase(), new UserCredentials(mongoURI.getUsername(), new String(mongoURI.getPassword())));
    }


}
