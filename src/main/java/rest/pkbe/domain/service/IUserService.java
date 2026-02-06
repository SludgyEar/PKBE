package rest.pkbe.domain.service;

import rest.pkbe.domain.model.User;

public interface IUserService {
    User register(User user);
    String []authenticate(String email, String password);
    String []refreshSession(String refreshToken);
    void logout(String refreshToken, Long userId, String accessToken);
}
