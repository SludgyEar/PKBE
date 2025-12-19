package rest.pkbe.domain.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rest.pkbe.domain.model.Tag;
import rest.pkbe.domain.repository.TagRepository;
import rest.pkbe.domain.service.ITagService;

@Service
public class TagServiceImpl implements ITagService{

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public Tag getOrCreate(String name) {
        /**
         * Busca un tag por su nombre, si no existe lo crea
         */
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                });
    }
    
}
