package com.Validators;

import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionsValidator {

    public void validateFundsTransfer(Response response, SoftAssert softAssert, int expectedFromAccountId,
                                      int expectedToAccountId, BigDecimal expectedAmount){
        String resp = response.asString();

        Pattern pattern = Pattern.compile("transferred \\$(\\d+\\.\\d+) from account #(\\d+) to account #(\\d+)");

        Matcher matcher = pattern.matcher(resp);

        if (matcher.find()) {

            BigDecimal amount = new BigDecimal(matcher.group(1));
            String fromAccountId = matcher.group(2);
            String toAccountId = matcher.group(3);

            softAssert.assertEquals(amount.compareTo(expectedAmount), 0, "Transfer amount mismatch");
            softAssert.assertEquals(fromAccountId, String.valueOf(expectedFromAccountId), "Source account ID mismatch");
            softAssert.assertEquals(toAccountId, String.valueOf(expectedToAccountId), "Destination account ID mismatch");

        }
    }

    public void validateTransactionDetails(Response response, SoftAssert softAssert, int expectedAccountId,
                                           BigDecimal expectedDebitedAmount){

        List<Map<String, Object>> transactions = response.getBody().jsonPath().getList("$");

        softAssert.assertTrue(transactions.size() == 2, "Mismatch in transactions count");

        Map<String, Object> creditTransaction = transactions.stream()
                .filter(t -> "Credit".equals(t.get("type")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Credit transaction not found"));

        Map<String, Object> debitTransaction = transactions.stream()
                .filter(t -> "Debit".equals(t.get("type")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Debit transaction not found"));

        BigDecimal actualCreditAmount = new BigDecimal(creditTransaction.get("amount").toString());
        BigDecimal actualDebitAmount = new BigDecimal(debitTransaction.get("amount").toString());
        int actualAccountIdCredit = (int) creditTransaction.get("accountId");
        int actualAccountIdDebit = (int) debitTransaction.get("accountId");
        String creditDescription = creditTransaction.get("description").toString();
        String debitDescription = debitTransaction.get("description").toString();

        softAssert.assertEquals(actualAccountIdCredit, expectedAccountId);
        softAssert.assertEquals(actualAccountIdDebit, expectedAccountId);
        softAssert.assertEquals(actualCreditAmount.compareTo(new BigDecimal("100.00")), 0, "Mismatch in credit amount received during account creation");
        softAssert.assertEquals(actualDebitAmount.compareTo(expectedDebitedAmount), 0, "Mismatch in debit amount sent during funds transfer");
        softAssert.assertEquals(creditDescription, "Funds Transfer Received", "Credit description is wrong");
        softAssert.assertEquals(debitDescription, "Funds Transfer Sent", "Debit description is wrong");
    }

}
