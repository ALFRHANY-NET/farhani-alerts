package com.farhaninet.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SmsReceiver extends BroadcastReceiver {

    // التوكن ورقم المجموعة الخاص بك
    private static final String BOT_TOKEN = "8701040297:AAHVEOABaRyb-KoBgzNV09vetCqpj6_BJ70"; 
    private static final String CHAT_ID = "-1003805566378"; 

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("BotSettings", Context.MODE_PRIVATE);
                    String savedKeywords = sharedPreferences.getString("keywords", "حوالة,تم ايداع");
                    String[] keywordList = savedKeywords.split(",");

                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String senderNum = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();
                        boolean matchFound = false;

                        for (String keyword : keywordList) {
                            String cleanKeyword = keyword.trim();
                            if (!cleanKeyword.isEmpty() && messageBody.contains(cleanKeyword)) {
                                matchFound = true;
                                break; 
                            }
                        }

                        if (matchFound) {
                            String telegramMessage = "🔔 *إشعار مالي جديد - الفرحاني نت*\n\n" +
                                                     "📱 *من رقم:* " + senderNum + "\n" +
                                                     "📝 *الرسالة:*\n" + messageBody;
                            sendToTelegram(telegramMessage);
                        }
                    }
                }
            }
        }
    }

    private void sendToTelegram(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    String jsonPayload = "{\"chat_id\":\"" + CHAT_ID + "\", \"text\":\"" + message + "\", \"parse_mode\":\"Markdown\"}";
                    
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } catch (Exception e) {
                    Log.e("TelegramBot", "Error: " + e.getMessage());
                }
            }
        }).start();
    }
}

