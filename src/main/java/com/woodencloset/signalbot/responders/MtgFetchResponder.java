package com.woodencloset.signalbot.responders;

import com.woodencloset.signalbot.SignalBot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MtgFetchResponder implements SignalBot.Responder {

    private static final Pattern CARD_PATTERN = Pattern.compile("\\[\\[([^\\[\\]]*)\\]\\]");
    private static final Pattern ID_PATTERN = Pattern.compile("multiverseid=([0-9]*)");
    private static final String GATHERING_TEMPLATE = "[%s] http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=%s\n";
    private static final String GATHERING_URL = "http://gatherer.wizards.com/Pages/Card/Details.aspx?name=%s";

    @Override
    public String getResponse(String messageText) {
        String response = makeCollectiveCardSummaryResponse(messageText);

        if (response.length() > 0) {
            System.out.println(response);
            return response;
        }

        return null;
    }

    public static String makeCollectiveCardSummaryResponse(String message) {
        String summaryResponse = "";
        String[] possibleCards = findPossibleCardsInString(message);

        for (String possibleCard : possibleCards) {
            try {
                ValidCardResponse validityResponse = isValidCard(possibleCard);
                if (validityResponse.Valid) {
                    String link = makeCardLink(possibleCard, validityResponse.CardId);
                    summaryResponse = summaryResponse.concat(link);
                }
            } catch (Exception ex) {
                continue;
            }
        }

        return summaryResponse;
    }

    public static String[] findPossibleCardsInString(String text) {
        List<String> possibleCards = new ArrayList<String>();
        Matcher matcher = CARD_PATTERN.matcher(text); 
        while (matcher.find()) {
            String capturedCard = matcher.group(1);
            try {
                String encodedCapturedCard = URLEncoder.encode(capturedCard, "UTF-8");
                possibleCards.add(encodedCapturedCard);
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported Encoding Exception!");
            }
        }

        return possibleCards.toArray(new String[0]);
    }

    public static String makeCardLink(String cardName, String cardId) {
        try {
            String encodedCardId = URLEncoder.encode(cardId, "UTF-8");
            String cardLink = String.format(GATHERING_TEMPLATE, cardName, encodedCardId);
            return cardLink;
        } catch (UnsupportedEncodingException exception) {
            return "";
        }
    }

    public static ValidCardResponse isValidCard(String encodedCardName) throws Exception {
        ValidCardResponse result = new ValidCardResponse(false, "");
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            String urlToOpen = String.format(GATHERING_URL, encodedCardName);
            urlToOpen.replace("%", "%26");

            HttpGet httpGet = new HttpGet(urlToOpen);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };

            String responseBody = httpClient.execute(httpGet, responseHandler);    
            Matcher matcher = ID_PATTERN.matcher(responseBody);
            
            while (matcher.find()) {
                String cardId = matcher.group(1);
                result.Valid = true;
                result.CardId = cardId;
            }
        } finally {
            httpClient.close();
        }

        return result;
    }
}