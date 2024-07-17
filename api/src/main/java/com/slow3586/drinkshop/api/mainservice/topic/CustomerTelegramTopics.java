package com.slow3586.drinkshop.api.mainservice.topic;

public final class CustomerTelegramTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "telegram.customer";
    private static final String TRANSACTION = ".transaction";

    public final static String PROCESS_REQUEST = NAME + ".process.request";
    public final static String PROCESS_RESPONSE = NAME + ".process.response";
    public final static String PUBLISH_REQUEST = NAME + ".publish.request";

    public static final class Transaction {
        public final static String CREATED = NAME + ".publish.created";
        public final static String WITH_CUSTOMERS = NAME + ".publish.withcustomers";
        public final static String ENTRY = NAME + ".publish.entry";
    }
}
