package com.fancyinnovations.fancycore.utils;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.nio.charset.StandardCharsets;

public class MongoDBConnector {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private MongoClient client;
    private MongoDatabase database;


    public MongoDBConnector(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Encodes a string for use in MongoDB connection string username/password.
     * Percent-encodes special characters that could be used for injection.
     */
    private static String encodeForMongoConnectionString(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder encoded = new StringBuilder();
        for (char c : value.toCharArray()) {
            // Encode special characters that could break connection string parsing
            if (c == ':' || c == '@' || c == '/' || c == '?' || c == '#' || 
                c == '[' || c == ']' || c == '%' || c < 32 || c > 126) {
                // Percent-encode the character
                for (byte b : String.valueOf(c).getBytes(StandardCharsets.UTF_8)) {
                    encoded.append('%');
                    encoded.append(String.format("%02X", b & 0xFF));
                }
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }

    public boolean connect() {
        try {
            String connectionString;
            // Check if username and password are null or empty
            boolean hasCredentials = username != null && !username.isEmpty() && 
                                     password != null && !password.isEmpty();
            
            if (!hasCredentials) {
                // No credentials - host should be a valid hostname/IP (not encoded)
                connectionString = "mongodb://" + host + ":" + port;
            } else {
                // Encode username and password to prevent injection attacks
                // Host is not encoded as it should be a valid hostname/IP address
                String encodedUsername = encodeForMongoConnectionString(username);
                String encodedPassword = encodeForMongoConnectionString(password);
                connectionString = "mongodb://" + encodedUsername + ":" + encodedPassword + "@" + host + ":" + port;
            }

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .build();

            client = MongoClients.create(settings);
            database = client.getDatabase("FancyAnalytics");
        } catch (Exception e) {
            FancyCore core = FancyCore.get();
            if (core != null) {
                core.getFancyLogger().warn("Failed to connect to MongoDB", ThrowableProperty.of(e));
            } else {
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            }
            return false;
        }
        return true;
    }

    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                FancyCore core = FancyCore.get();
                if (core != null) {
                    core.getFancyLogger().warn("Failed to close MongoDB client", ThrowableProperty.of(e));
                } else {
                    System.err.println("Failed to close MongoDB client: " + e.getMessage());
                }
            } finally {
                client = null;
                database = null;
            }
        }
    }

    public boolean isConnected() {
        return client != null;
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("MongoDB database is not initialized. Call connect() first.");
        }
        return database;
    }
}
