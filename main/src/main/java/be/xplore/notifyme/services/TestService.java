package be.xplore.notifyme.services;

import org.springframework.stereotype.Service;

@Service
public class TestService {

  public int sum(int test, int test2) {
    return test + test2;
  }
}
