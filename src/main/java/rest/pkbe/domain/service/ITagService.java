package rest.pkbe.domain.service;

import rest.pkbe.domain.model.Tag;

public interface ITagService {

    Tag getOrCreate(String name);

}
