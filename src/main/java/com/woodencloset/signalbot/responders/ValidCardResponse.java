package com.woodencloset.signalbot.responders;

public class ValidCardResponse {
    public Boolean Valid;
    public String CardId;

    public ValidCardResponse(Boolean valid, String id) {
        Valid = valid;
        CardId = id;
    }
}