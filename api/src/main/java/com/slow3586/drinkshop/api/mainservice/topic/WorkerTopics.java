package com.slow3586.drinkshop.api.mainservice.topic;

public final class WorkerTopics {
    private static final String ERROR = ".error";
    private static final String NAME = "order";
    private static final String TRANSACTION = ".transaction";
    private static final String REQUEST = ".request";
    private static final String STATUS = ".status";
    private static final String RESPONSE = ".request";
    public final static String REQUEST_LOGIN = NAME + REQUEST + ".login";
    public final static String REQUEST_TOKEN = NAME + REQUEST + ".token";
    public final static String REQUEST_LOGIN_RESPONSE = REQUEST_LOGIN + RESPONSE;
    public final static String REQUEST_TOKEN_RESPONSE = REQUEST_TOKEN + RESPONSE;
}
