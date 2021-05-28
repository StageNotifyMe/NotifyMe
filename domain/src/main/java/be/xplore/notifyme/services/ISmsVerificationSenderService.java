package be.xplore.notifyme.services;

public interface ISmsVerificationSenderService {

  void send(String title, String body, String phoneNo);
}
