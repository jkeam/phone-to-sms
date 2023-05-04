package io.chattabot.services;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.VoiceResponse;
import com.twilio.exception.ApiException;
import com.twilio.type.PhoneNumber;

import io.chattabot.models.Phone;
import io.quarkus.logging.Log;

@ApplicationScoped
public class TwilioService {
    @ConfigProperty(name = "twilio.account.sid", defaultValue = "")
    String accountSid;

    @ConfigProperty(name = "twilio.auth.token", defaultValue = "")
    String authToken;

    @ConfigProperty(name = "default.message.voice", defaultValue = "Hi! Thanks for calling, but we prefer text. We are texting you now.")
    String voiceMessage;

    @ConfigProperty(name = "default.message.text", defaultValue = "Thanks for calling, but text us back here!")
    String textMessage;
    
    /**
     * Lookup the configuration by phone.
     * @param phoneNumber The phone number to lookup
     * @return Phone the phone number object
     */
    public Phone lookupPhone(String phoneNumber) {
        final Phone phoneConfig = new Phone(phoneNumber, voiceMessage, textMessage);
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            Phone fetchedPhoneConfig = Phone.findByPhoneNumber(phoneNumber).orElse(phoneConfig);
            String configVoiceMessage = fetchedPhoneConfig.voiceMessage;
            String configTextMessage = fetchedPhoneConfig.textMessage;
            String configVoice = fetchedPhoneConfig.voice;

            // need to do this check in case the columns are null or blank
            phoneConfig.propertyName = fetchedPhoneConfig.propertyName;
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
    
    /**
     * Send an SMS text message.
     * @param toNumber Phone number to text.
     * @param fromNumber Phone number the text should come from.
     * @param textMessage Message to send.
     */
    public void sendSms(String toNumber, String fromNumber, String textMessage) {
        if (accountSid == null || accountSid.isBlank()) {
            Log.error("Missing twilio account sid.");
            return;
        }
        if (authToken == null || authToken.isBlank()) {
            Log.error("Missing twilio auth token.");
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
                Log.warn("Uh oh, looks like this caller can't receive SMS messages.");
            }
        }
    }

    /**
     * Send a voice message.
     * @param phoneConfig Phone configuration object.
     * @return xml string that Twilio can process.
     */
    public String sendVoice(Phone phoneConfig) {
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
