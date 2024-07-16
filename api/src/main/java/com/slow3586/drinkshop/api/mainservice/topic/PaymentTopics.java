package com.slow3586.drinkshop.api.mainservice.topic;

public final class PaymentTopics {
    private static final String NAME = "payment";
    private static final String REQUEST = ".request";
    private static final String STATUS = ".status";
    public final static String REQUEST_SYSTEM = NAME + REQUEST + ".system";
    public final static String REQUEST_SYSTEM_RESPONSE = REQUEST_SYSTEM + ".response";
    public final static String STATUS_PAID = NAME + STATUS + ".paid";
}
