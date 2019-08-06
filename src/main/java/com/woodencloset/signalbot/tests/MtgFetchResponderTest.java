package com.woodencloset.signalbot.tests;

import com.woodencloset.signalbot.responders.MtgFetchResponder;

public class MtgFetchResponderTest {

    private void testSingleValidCard() {
        String body = "I like [[Brainstorm]] a lot. It's a fair card.";

        System.out.println("------------- TESTING SINGLE VALID CARD -------------");
        System.out.println("Message: \"" + body);
        String response = MtgFetchResponder.makeCollectiveCardSummaryResponse(body);
        if (response.length() > 0) {
            System.out.println("Response: \"" + response);
        }
        System.out.println();
    }

    private void testSingleInvalidCard() {
        String body = "I like [[ Masked HERO Anki]] a lot. It's my favorite Yu-Gi-Oh! card.";
        
        System.out.println("------------- TESTING SINGLE INVALID CARD -------------");
        System.out.println("Message: \"" + body);
        String response = MtgFetchResponder.makeCollectiveCardSummaryResponse(body);
        if (response.length() > 0) {
            System.out.println("Response: \"" + response);
        }
        System.out.println();
    }
    
    private void testMultipleValidCards() {
        String body = "[[Wrath of God]] and [[Damnation]] have very similiar arts.";
        
        System.out.println("------------- TESTING MULTIPLE VALID CARD -------------");
        System.out.println("Message: \"" + body);
        String response = MtgFetchResponder.makeCollectiveCardSummaryResponse(body);
        if (response.length() > 0) {
            System.out.println("Response: \"" + response);
        }
        System.out.println();
    }

    private void testMultipleWithInvalidCard() {
        String body = "[[Wrath of Goo]] and [[Damna]] have very similiar arts.";
        
        System.out.println("------------- TESTING MULTIPLE INVALID CARD -------------");
        System.out.println("Message: \"" + body);
        String response = MtgFetchResponder.makeCollectiveCardSummaryResponse(body);
        if (response.length() > 0) {
            System.out.println("Response: \"" + response);
        }
        System.out.println();
    }

}