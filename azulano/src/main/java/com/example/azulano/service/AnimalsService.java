package com.example.azulano.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.azulano.dto.Animals.AnimalsDTO;
import com.example.azulano.model.Animals.Animals;
import com.example.azulano.repository.Animals.AnimalsRepository;


@Service
public class AnimalsService {
    @Autowired
    AnimalsRepository repository;

    @Autowired
    PagedResourcesAssembler<Animals> pageAssembler;


    public Animals saveAnimals(AnimalsDTO data) {
    Animals salvarAnimais = new Animals(data);
        return repository.save(salvarAnimais);
    }

    public EntityModel<Animals> searchById(Long id){
        var animal = repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Animal não encontrado")
        );

        return animal.toEntityModel();
    };

    public PagedModel<EntityModel<Animals>> findByPages(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String family,
        @PageableDefault(size = 5,direction = Direction.DESC) Pageable pageable
    ){
        Page<Animals> page = null;

        if(name != null && family != null){
            page = repository.findByNameAndFamily(name, family, pageable);
        }
        if(name != null){
            page = repository.findByName(name, pageable);
        }
        if(family != null){
            page = repository.findByFamily(family, pageable);
        }

        if(page == null){
            page = repository.findAll(pageable);
        }

        return pageAssembler.toModel(page, Animals::toEntityModel);
    }

    public ResponseEntity<EntityModel<Animals>> created(AnimalsDTO data){
        Animals newAnimals = new Animals(data);
        repository.save(newAnimals);

        return ResponseEntity
        .created(newAnimals.toEntityModel().getRequiredLink("self").toUri())
        .body(newAnimals.toEntityModel());
        
    }

        public ResponseEntity<EntityModel<Animals>> put( Long id, AnimalsDTO data) {
        verifyExistsId(id);
        Animals putAnimals = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animals não encontrado"));
        BeanUtils.copyProperties(data, putAnimals, "id");
        repository.save(putAnimals);
        return ResponseEntity
                            .ok(putAnimals.toEntityModel());
    }

    public ResponseEntity<Void> destroy(Long id){
       repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


     public void verifyExistsId(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Não existe id informado"));
    }
}
