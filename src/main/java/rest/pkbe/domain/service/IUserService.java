package rest.pkbe.domain.service;

import rest.pkbe.domain.model.User;

public interface IUserService {
    User register(User user);
    User authenticate(String email, String password);
}
