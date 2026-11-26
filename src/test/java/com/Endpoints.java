package com;

public class Endpoints {

    //Customer controller APIs
    public static final String FETCH_CUSTOMER_DETAILS = "/customers/{customerId}";

    //Accounts controller APIs
    public static final String CREATE_ACCOUNT = "/createAccount";

    //Transaction controller APIs
    public static final String TRANSFER_FUNDS = "/transfer";
    public static final String TRANSACTIONS_DETAILS = "/accounts/{accountId}/transactions";
}
