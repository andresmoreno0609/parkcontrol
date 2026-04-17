package com.parkcontrol.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public abstract class BaseCrudService<Entity, REQ, RES, ID> {

    protected abstract JpaRepository<Entity, ID> getRepository();

    protected abstract Entity toEntity(REQ request);
    protected abstract RES toResponse(Entity entity);

    protected void updateEntity(REQ request, Entity entity) {
        // Override in subclasses for update logic
    }

    public RES create(REQ request) {
        Entity entity = toEntity(request);
        Entity saved = getRepository().save(entity);
        return toResponse(saved);
    }

    public Optional<Entity> findById(ID id) {
        return getRepository().findById(id);
    }

    public RES findByIdResponse(ID id) {
        return getRepository().findById(id).map(this::toResponse).orElse(null);
    }

    public Page<Entity> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    public RES update(ID id, REQ request) {
        return getRepository().findById(id)
                .map(entity -> {
                    updateEntity(request, entity);
                    Entity updated = getRepository().save(entity);
                    return toResponse(updated);
                })
                .orElse(null);
    }

    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    public boolean existsById(ID id) {
        return getRepository().existsById(id);
    }
}