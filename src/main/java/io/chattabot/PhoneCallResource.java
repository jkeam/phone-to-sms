package io.chattabot;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import io.chattabot.models.Phone;
import io.chattabot.services.TwilioService;
import io.quarkus.logging.Log; 

@Path("/")
public class PhoneCallResource {
    @Inject
    TwilioService twilioService;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Log.info("Hello Endpoint");
        return "Phone Call";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_XML)
    public String webhook(@FormParam("From") String caller, @FormParam("To") String twilioNumber) {
        Log.info("Webhook Endpoint");
        if (Log.isDebugEnabled()) {
            Log.debug(String.format("from: %s, to: %to", caller, twilioNumber));
        }

        Phone phoneConfig = twilioService.lookupPhone(twilioNumber);
        if (caller != null && !caller.isBlank() && twilioNumber != null && !twilioNumber.isBlank()) {
            twilioService.sendSms(caller, twilioNumber, phoneConfig.textMessage);
        }

        return twilioService.sendVoice(phoneConfig);
    }
}