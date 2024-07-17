package com.slow3586.drinkshop.api.mainservice.topic;

public final class OrderTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "order";
    private static final String TRANSACTION = ".transaction";
    private static final String REQUEST = ".request";
    private static final String RESPONSE = ".request";
    private static final String STATUS = ".status";

    public final static String TRANSACTION_CREATED = NAME + TRANSACTION + ".created";

    public final static String TRANSACTION_PRODUCT = NAME + TRANSACTION + ".product";
    public final static String TRANSACTION_SHOP = NAME + TRANSACTION + ".shop";
    public final static String TRANSACTION_CUSTOMER = NAME + TRANSACTION + ".customer";

    public final static String TRANSACTION_INVENTORY = NAME + TRANSACTION + ".inventory";
    public final static String TRANSACTION_PAYMENT = NAME + TRANSACTION + ".payment";

    public final static String TRANSACTION_PUBLISH = NAME + TRANSACTION + ".publish";

    public final static String TRANSACTION_PAID = NAME + TRANSACTION + ".paid";

    public final static String TRANSACTION_COMPLETED = NAME + TRANSACTION + ".completed";

    public final static String TRANSACTION_ERROR = NAME + TRANSACTION + ERROR;
    public final static String STATUS_CANCELLED = NAME + STATUS + ".cancelled";
    public final static String STATUS_COMPLETED = NAME + STATUS + ".completed";
    public final static String STATUS_AWAITINGPAYMENT = NAME + STATUS + ".awaitingpayment";
    public final static String STATUS_ERROR = NAME + STATUS + ERROR;
    public final static String STATUS_PAID = NAME + STATUS + ".paid";
    public final static String REQUEST_CREATE = NAME + REQUEST + ".create";
    public final static String REQUEST_COMPLETED = NAME + REQUEST + ".completed";
    public final static String REQUEST_CANCEL = NAME + REQUEST + ".cancel";
    public final static String REQUEST_CREATE_RESPONSE = REQUEST_CREATE + RESPONSE;
    public final static String REQUEST_COMPLETED_RESPONSE = REQUEST_COMPLETED + RESPONSE;
    public final static String REQUEST_CANCEL_RESPONSE = REQUEST_CANCEL + RESPONSE;
}
