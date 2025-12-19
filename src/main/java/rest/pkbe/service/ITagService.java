package rest.pkbe.service;

import rest.pkbe.model.Tag;

public interface ITagService {

    Tag getOrCreate(String name);

}
