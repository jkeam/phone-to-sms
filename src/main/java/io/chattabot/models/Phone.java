package io.chattabot.models;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "phones")
public class Phone extends PanacheEntity {
  public Phone() {
    this.voice = "ALICE";
  }

  /**
   * Full constructor
   * @param phoneNumber The property phone number
   * @param propertyName The property name
   * @param voiceMessage The voice message
   * @param textMessage The text message
   * @param voice The voice to use
   */
  public Phone(String phoneNumber, String propertyName, String voiceMessage, String textMessage, String voice) {
    this.phoneNumber = phoneNumber;
    this.propertyName = propertyName;
    this.voiceMessage = voiceMessage;
    this.textMessage = textMessage;
    this.voice = voice;
  }

  /**
   * Constructor with just the necessary params.
   * @param phoneNumber The property phone number
   * @param voiceMessage The voice message
   * @param textMessage The text message
   */
  public Phone(String phoneNumber, String voiceMessage, String textMessage) {
    this.phoneNumber = phoneNumber;
    this.voiceMessage = voiceMessage;
    this.textMessage = textMessage;
    this.voice = "ALICE";
  }

  @Column(name = "phone_number", unique = true, nullable = false, length = 12)
  public String phoneNumber;

  @Column(name = "property_name", nullable = true, length = 256)
  public String propertyName;

  @Column(name = "voice_message", nullable = true, length = 1024)
  public String voiceMessage;

  @Column(name = "text_message", nullable = true, length = 256)
  public String textMessage;

  public String voice;

  /**
   * Find phone by phone number
   * @param phoneNumber string phone number to search by
   * @return matching Phone object
   */
  public static Optional<Phone> findByPhoneNumber(String phoneNumber) {
    return find("phoneNumber", phoneNumber).firstResultOptional();
  }
}
