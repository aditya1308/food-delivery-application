package com.application.food.delivery.util;

import com.application.food.delivery.exception.InvalidOtpException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OtpService {

    @Value("${app.otp.dev-mode}")
    private boolean devMode;


    private final JavaMailSender mailSender;

    Logger log = LoggerFactory.getLogger(OtpService.class);

    @Getter
    private static Map<String, Boolean> verificationStatus = new ConcurrentHashMap<>();

    @Value("${fast2.sms.api-key}")
    private String apiKey;

    @Value("${fast2.sms.url}")
    private String smsUrl;

    @Value("${fast2.sms.content-type}")
    private String contentType;

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_DURATION = 5 * 60 * 1000;

    // Store OTPs for both email and mobile (thread-safe)
    private final Map<String, OtpInfo> otpStore = new ConcurrentHashMap<>();

    @Autowired
    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Generate random OTP
    private String generateOtp() {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return otp.toString();
    }

    // ✅ Send OTP via Email
    public String sendOtpToEmail(String email) {
        String otp = generateOtp();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("service.cab.app@gmail.com");
            message.setTo(email);
            message.setSubject("FDA - Email OTP Verification");
            message.setText("Your OTP for verification is: " + otp + "\nThis OTP is valid for 5 minutes.");
            mailSender.send(message);

            otpStore.put(email, new OtpInfo(otp, System.currentTimeMillis()));
            return "✅ OTP sent successfully to email.";
        } catch (Exception e) {
            return "❌ Failed to send OTP email: " + e.getMessage();
        }
    }

    // ✅ Send OTP via Mobile (Fast2SMS API)
    public String sendOtpToMobile(String mobileNo) {
        String otp = generateOtp(); // Generate random OTP
        if (devMode) {
            otpStore.put(mobileNo, new OtpInfo(otp, System.currentTimeMillis()));
            verificationStatus.put(mobileNo, true);
            log.info("\uD83D\uDCA1 [DEV MODE] OTP for {}: {}", mobileNo, otp);
            return "✅ [DEV MODE] OTP generated: " + otp;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {


            JSONObject requestBody = new JSONObject();
            requestBody.put("route", "q");
            requestBody.put("message", "Hello! Your OTP is: " + otp);
            requestBody.put("numbers", mobileNo);

            HttpPost post = new HttpPost(smsUrl);
            post.addHeader("accept", MediaType.APPLICATION_JSON_VALUE);
            post.addHeader("authorization", apiKey);
            post.addHeader("content-type", MediaType.APPLICATION_JSON_VALUE);

            post.setEntity(new StringEntity(requestBody.toString()));

            HttpResponse response = httpClient.execute(post);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(responseString);

            if (jsonResponse.has("return") && jsonResponse.getBoolean("return")) {
                otpStore.put(mobileNo, new OtpInfo(otp, System.currentTimeMillis()));
                return "✅ OTP sent successfully to mobile: " + mobileNo;
            }
 //       else {
//                return "❌ Failed to send OTP: " + jsonResponse.optString("message");
//            }

        } catch (RuntimeException | IOException e) {
            throw new InvalidOtpException("❌ Error sending OTP: " + e.getMessage());
        }
        return "❌ Failed to send OTP";
    }


    // ✅ Verify OTP (for both email & phone)
    public boolean verifyOtp(Map<String, String> req) {
        String email = req.getOrDefault("email", "");
        String mobileNo = req.getOrDefault("mobileNo", "");
        String emailOtp = req.getOrDefault("emailOtp", "");
        String mobileOtp = req.getOrDefault("mobileOtp", "");
        verificationStatus.put(mobileNo, false);
        verificationStatus.put(email, false);

        OtpInfo info = otpStore.get(email) != null ? otpStore.get(email) : otpStore.get(mobileNo);
        if (info == null) return false;

        long currentTime = System.currentTimeMillis();
        if ((currentTime - info.timestamp) > OTP_EXPIRY_DURATION) {
            otpStore.remove(email);
            otpStore.remove(mobileNo);
            return false;
        }

        boolean isbothOtpValid = false;
        if (info.otp.equals(emailOtp)) {
            verificationStatus.put(email, true);
            otpStore.remove(email);
            isbothOtpValid = true;
            log.info("Email OTP verified");

        }

        if(info.otp.equals(mobileOtp)){
            verificationStatus.put(mobileNo, true);
            otpStore.remove(mobileNo);
            isbothOtpValid = true;
            log.info("Mobile OTP verified");
        }

        return isbothOtpValid;
    }

    // ✅ Inner class to store OTP & timestamp
    private static class OtpInfo {
        String otp;
        long timestamp;

        OtpInfo(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }

    public String getStoredOtp(String email) {
        OtpInfo info = otpStore.get(email);
        return (info != null) ? info.otp : null;
    }


}
