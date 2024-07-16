package com.slow3586.drinkshop.api.mainservice.topic;

public final class PromoTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "promo";
    private static final String TRANSACTION = ".transaction";
    public static final String CREATE_REQUEST = NAME + ".request.create";
    public final static String TRANSACTION_CREATED = NAME + TRANSACTION + ".created";
}
