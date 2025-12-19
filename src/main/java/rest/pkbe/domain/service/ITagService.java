package rest.pkbe.domain.service;

import rest.pkbe.domain.model.Tag;
import rest.pkbe.domain.model.User;

public interface ITagService {

    Tag getOrCreate(String name, User user);

}
