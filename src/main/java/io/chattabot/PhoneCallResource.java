package io.chattabot;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

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
        
        if (caller != null && !caller.isBlank() && twilioNumber != null && !twilioNumber.isBlank()) {
          sendSms(caller, twilioNumber);
        }

        VoiceResponse twiml = new VoiceResponse.Builder()
            .say(new Say.Builder(System.getenv("VOICE_MESSAGE"))
                  .voice(Say.Voice.ALICE)
                  .build())
            .build();

        return twiml.toXml();
    }
    
    private void sendSms(String toNumber, String fromNumber) {
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
                         System.getenv("TEXT_MESSAGE"))
                .create();
        } catch (ApiException e) {
            if (e.getCode() == 21614) {
                System.out.println("Uh oh, looks like this caller can't receive SMS messages.");
            }
        }
    }
}