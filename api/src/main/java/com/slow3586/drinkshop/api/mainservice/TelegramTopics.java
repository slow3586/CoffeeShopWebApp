package com.slow3586.drinkshop.api.mainservice;

public final class TelegramTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "telegram";
    private static final String TRANSACTION = ".transaction";

    public final static String CUSTOMER_PROCESS_REQUEST = "telegram.customer.process.request";
    public final static String CUSTOMER_PROCESS_RESPONSE = "telegram.customer.process.response";
    public final static String CUSTOMER_PUBLISH_REQUEST = "telegram.customer.publish.request";
    public final static String CUSTOMER_PUBLISH_CREATED = "telegram.customer.publish.created";
    public final static String CUSTOMER_PUBLISH_BOT = "telegram.customer.publish.bot";
}
