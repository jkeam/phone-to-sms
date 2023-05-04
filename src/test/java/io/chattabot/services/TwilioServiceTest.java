package io.chattabot.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.twilio.Twilio;
import com.twilio.http.HttpClient;
import com.twilio.http.NetworkHttpClient;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import io.chattabot.models.Phone;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.panache.mock.PanacheMock;

@QuarkusTest
public class TwilioServiceTest {
    
    @Inject
    TwilioService twilioService;
    
    @Test
    public void testLookupPhone() {
        // when
        String phoneNumber = "+15551112222";
        Phone expected = new Phone(phoneNumber, "Hi! Thanks for calling, but we prefer text. We are texting you now.", "Thanks for calling, but text us back here!");
        Optional<Phone> optionalExpected = Optional.of(expected);
        PanacheMock.mock(Phone.class);
        Mockito
            .when(Phone.findByPhoneNumber(phoneNumber))
            .thenReturn(optionalExpected);
        
        // test
        Phone actual = twilioService.lookupPhone(phoneNumber);
        
        // verify
        Assertions.assertEquals(expected.phoneNumber, actual.phoneNumber);
        Assertions.assertEquals(expected.propertyName, actual.propertyName);
        Assertions.assertEquals(expected.textMessage, actual.textMessage);
        Assertions.assertEquals(expected.voiceMessage, actual.voiceMessage);
        Assertions.assertEquals(expected.voice, actual.voice);
        PanacheMock.verify(Phone.class, Mockito.times(1));
        Phone.findByPhoneNumber(phoneNumber);
        PanacheMock.verifyNoMoreInteractions(Phone.class);
    }
    
    @Test
    public void testSendVoice() {
        Phone phone = new Phone("+15551112222", "Hi! Thanks for calling, but we prefer text. We are texting you now.", "Thanks for calling, but text us back here!");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say voice=\"alice\">Hi! Thanks for calling, but we prefer text. We are texting you now.</Say></Response>";
        String actual = twilioService.sendVoice(phone);
        Assertions.assertEquals(expected, actual);
    }
    
    @Test
    public void testSendSms() {
        twilioService.sendSms("+15551112222", "+15552223333", "Hi!");
    }
}
