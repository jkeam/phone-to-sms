package io.chattabot;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import io.chattabot.models.Phone;
import io.quarkus.logging.Log; 

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.VoiceResponse;
import com.twilio.exception.ApiException;
import com.twilio.type.PhoneNumber;

@Path("/")
public class PhoneCallResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Phone Call";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_XML)
    public String webhook(@FormParam("From") String caller, @FormParam("To") String twilioNumber) {

        if (Log.isDebugEnabled()) {
            Log.debug(String.format("From phone: %s", caller));
            Log.debug(String.format("From phone: %s", twilioNumber));
        }
        
        Phone phoneConfig = lookupPhone(caller);
        if (caller != null && !caller.isBlank() && twilioNumber != null && !twilioNumber.isBlank()) {
            sendSms(caller, twilioNumber, phoneConfig.textMessage);
        }

        return sendVoice(phoneConfig);
    }

    private Phone lookupPhone(String caller) {
        final String voiceMessage = System.getenv("VOICE_MESSAGE");
        final String textMessage = System.getenv("TEXT_MESSAGE");
        Phone phoneConfig = new Phone(caller, voiceMessage, textMessage);
        if (caller != null && !caller.isBlank()) {
            Phone fetchedPhoneConfig = Phone.findByPhoneNumber(caller).orElse(phoneConfig);
            String configVoiceMessage = fetchedPhoneConfig.voiceMessage;
            String configTextMessage = fetchedPhoneConfig.textMessage;
            String configVoice = fetchedPhoneConfig.voice;

            // need to do this check in case the columns are null or blank
            if (configVoiceMessage != null && !configVoiceMessage.isBlank()) {
                phoneConfig.voiceMessage = configVoiceMessage;
            }
            if (configTextMessage != null && !configTextMessage.isBlank()) {
                phoneConfig.textMessage = configTextMessage;
            }
            if (configVoice != null && !configVoice.isBlank()) {
                phoneConfig.voice = configVoice;
            }
        }
        return phoneConfig;
    }
    
    private void sendSms(String toNumber, String fromNumber, String textMessage) {
        String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
        String authToken = System.getenv("TWILIO_AUTH_TOKEN");
        if (accountSid == null || accountSid.isBlank() || authToken == null || authToken.isBlank()) {
            return;
        }
        try {
            Twilio.init(accountSid, authToken);
            Message
                .creator(new PhoneNumber(toNumber),
                         new PhoneNumber(fromNumber),
                         textMessage)
                .create();
        } catch (ApiException e) {
            if (e.getCode() == 21614) {
                System.out.println("Uh oh, looks like this caller can't receive SMS messages.");
            }
        }
    }

    private String sendVoice(Phone phoneConfig) {
        // Check for allowed voices
        Say.Voice voice = Say.Voice.ALICE;
        switch(phoneConfig.voice) {
            case "MAN":   voice = Say.Voice.MAN;
                          break;
            case "WOMAN": voice = Say.Voice.WOMAN;
                          break;
            default:      voice = Say.Voice.ALICE;
                          break;
        }
        VoiceResponse twiml = new VoiceResponse.Builder()
            .say(new Say.Builder(phoneConfig.voiceMessage)
                  .voice(voice)
                  .build())
            .build();

        return twiml.toXml();
    }
}