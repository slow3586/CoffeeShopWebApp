package com.slow3586.drinkshop.api.mainservice;

public final class OrderTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "order";
    private static final String TRANSACTION = ".transaction";
    private static final String REQUEST = ".request";
    private static final String STATUS = ".status";
    public final static String TRANSACTION_CREATED = NAME + TRANSACTION + ".created";
    public final static String TRANSACTION_PRODUCT = NAME + TRANSACTION + ".product";
    public final static String TRANSACTION_INVENTORY = NAME + TRANSACTION + ".inventory";
    public final static String TRANSACTION_SHOP = NAME + TRANSACTION + ".shop";
    public final static String TRANSACTION_PAYMENT = NAME + TRANSACTION + ".payment";
    public final static String TRANSACTION_CUSTOMER = NAME + TRANSACTION + ".customer";
    public final static String TRANSACTION_PRODUCT_ERROR = TRANSACTION_PRODUCT + ERROR;
    public final static String TRANSACTION_INVENTORY_ERROR = TRANSACTION_INVENTORY + ERROR;
    public final static String TRANSACTION_SHOP_ERROR = TRANSACTION_SHOP + ERROR;
    public final static String TRANSACTION_PAYMENT_ERROR = TRANSACTION_PAYMENT + ERROR;
    public final static String TRANSACTION_CUSTOMER_ERROR = TRANSACTION_CUSTOMER + ERROR;
    public final static String STATUS_CANCELLED = NAME + STATUS + ".cancelled";
    public final static String STATUS_COMPLETED = NAME + STATUS + ".completed";
    public final static String STATUS_AWAITINGPAYMENT = NAME + STATUS + ".awaitingpayment";
    public final static String STATUS_ERROR = NAME + STATUS + ".error";
    public final static String STATUS_PAID = NAME + STATUS + ".paid";
    public final static String REQUEST_CREATE = NAME + REQUEST + ".create";
    public final static String REQUEST_COMPLETE = NAME + REQUEST + ".complete";
}
