package io.chattabot.models;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "phones")
public class Phone extends PanacheEntity {

  /**
   * Default constructor
   */
  public Phone() {
    this.voice = "ALICE";
  }

  /**
   * Full constructor
   * @param phoneNumber The property phone number
   * @param propertyName The property name
   * @param voiceMessage The voice message
   * @param textMessage The text message
   * @param voice The voice to use in the voice message
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

  @Column(name = "property_name", nullable = false, length = 256)
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

  /**
   * Override hashCode.
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
    result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
    result = prime * result + ((voiceMessage == null) ? 0 : voiceMessage.hashCode());
    result = prime * result + ((textMessage == null) ? 0 : textMessage.hashCode());
    result = prime * result + ((voice == null) ? 0 : voice.hashCode());
    return result;
  }

  /**
   * Override equals.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Phone other = (Phone) obj;
    if (phoneNumber == null) {
      if (other.phoneNumber != null)
        return false;
    } else if (!phoneNumber.equals(other.phoneNumber))
      return false;
    if (propertyName == null) {
      if (other.propertyName != null)
        return false;
    } else if (!propertyName.equals(other.propertyName))
      return false;
    if (voiceMessage == null) {
      if (other.voiceMessage != null)
        return false;
    } else if (!voiceMessage.equals(other.voiceMessage))
      return false;
    if (textMessage == null) {
      if (other.textMessage != null)
        return false;
    } else if (!textMessage.equals(other.textMessage))
      return false;
    if (voice == null) {
      if (other.voice != null)
        return false;
    } else if (!voice.equals(other.voice))
      return false;
    return true;
  }
}
