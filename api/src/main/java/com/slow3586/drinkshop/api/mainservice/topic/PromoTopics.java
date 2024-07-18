package com.slow3586.drinkshop.api.mainservice.topic;

public final class PromoTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "promo";
    private static final String TRANSACTION = ".transaction";
    public static final String CREATE_REQUEST = NAME + ".request.create";

    public static final class Transaction {
        public final static String CREATED = NAME + TRANSACTION + ".created";
        public final static String PUBLISH = NAME + TRANSACTION + ".publish";
        public final static String ERROR = NAME + TRANSACTION + PromoTopics.ERROR;
    }
}
