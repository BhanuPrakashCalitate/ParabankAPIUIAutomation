package com.Utils.CommonUtils;

import java.util.Arrays;

public enum AccountType {

    CHECKING(0),
    SAVINGS(1);

    private final int code;

    AccountType(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static AccountType fromCode(int code){
        return Arrays.stream(values())
                .filter(type -> type.code==code)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid account type: " + code));
    }
}
