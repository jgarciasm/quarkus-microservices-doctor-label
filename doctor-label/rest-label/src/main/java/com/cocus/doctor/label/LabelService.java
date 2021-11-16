package com.cocus.doctor.label;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;

@ApplicationScoped
@Transactional(REQUIRED)
public class LabelService {

    @Transactional(SUPPORTS)
    @CacheResult(cacheName = "all-labels-cache")
    public List<Label> findAllLabels() {
        return Label.listAll();
    }
    
    @CacheInvalidate(cacheName = "all-labels-cache")
    public void invalidateAlllabelsCache() {}

    @Transactional(SUPPORTS)
    @CacheResult(cacheName = "label-cache")
    public Label findLabelById(Long id) {
        return Label.findById(id);
    }
    
    @CacheInvalidate(cacheName = "label-cache")
    public void invalidateLabelByIdCache(Long id) {}

    public Label persistLabel(@Valid Label label) {
        Label.persist(label);
        invalidateAlllabelsCache();
        return label;
    }

    @CacheInvalidate(cacheName = "label-cache")
    public Label updateLabel(@Valid Label label) {
    	
    	Optional<Label> optional = Label.findByIdOptional(label.id);
    	Label entity = optional.orElseThrow(() -> new NotFoundException());
    	
        entity.code = label.code;
        entity.description = label.description;
        invalidateAlllabelsCache();
        invalidateLabelByIdCache(label.id);
        return entity;
    }

    @CacheInvalidate(cacheName = "all-labels-cache")
    public void deleteLabel(Long id) {
    	
    	Optional<Label> optional = Label.findByIdOptional(id);
    	Label label = optional.orElseThrow(() -> new NotFoundException());

        invalidateAlllabelsCache();
        invalidateLabelByIdCache(id);
        label.delete();
    }

}
