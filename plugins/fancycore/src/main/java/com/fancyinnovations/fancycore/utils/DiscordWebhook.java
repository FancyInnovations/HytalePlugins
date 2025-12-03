package com.fancyinnovations.fancycore.utils;

import com.fancyinnovations.fancycore.api.discord.Message;
import de.oliver.fancyanalytics.sdk.utils.HttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class DiscordWebhook {

    public static void sendMsg(String webhookUrl, Message msg) {
        HttpRequest request = new HttpRequest(webhookUrl)
                .withMethod("POST")
                .withHeader("Content-Type", "application/json")
                .withBody(msg);

        try {
            HttpResponse<String> resp = request.send();
            if (resp.statusCode() != 204) {
                System.out.println("Failed to send message with discord webhook: " + resp.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Failed to send webhook");
            e.printStackTrace();
        }
    }

}
