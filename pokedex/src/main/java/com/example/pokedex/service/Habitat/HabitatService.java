package com.example.pokedex.service.Habitat;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

import com.example.pokedex.dto.Habitat.HabitatDTO;
import com.example.pokedex.model.Habitat.Habitat;
import com.example.pokedex.repository.Habitat.HabitatRepository;

@Service
public class HabitatService {
    @Autowired
    HabitatRepository repository;
    
    @Autowired
    PagedResourcesAssembler<Habitat> pageAssembler;


    public EntityModel<Habitat> searchById(Long id){
        var habitat = repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Animal não encontrado")
        );

        return habitat.toEntityModel();
    };

     public PagedModel<EntityModel<Habitat>> findByPages(
        @RequestParam(required = false) String nameHabitat,
        @PageableDefault(size = 5,direction = Direction.DESC) Pageable pageable
    ){
        Page<Habitat> page = null;

        if(nameHabitat != null){
            page = repository.findByNameHabitat(nameHabitat, pageable);
        }

        if(page == null){
            page = repository.findAll(pageable);
        }

        return pageAssembler.toModel(page, Habitat::toEntityModel);
    }


     public Habitat created(HabitatDTO data){
        Habitat newHabitat = new Habitat(data);
        return repository.save(newHabitat);
    }


    
        public ResponseEntity<EntityModel<Habitat>> put( Long id, HabitatDTO data) {
        verifyExistsId(id);
        Habitat putHabitat = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
        BeanUtils.copyProperties(data, putHabitat, "id");
        repository.save(putHabitat);
        return ResponseEntity
                            .created(putHabitat.toEntityModel().getRequiredLink("self").toUri())
                            .body(putHabitat.toEntityModel());
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
    

