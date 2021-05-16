package com.game.repository;

import com.game.entity.PlayerEntity;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepo extends CrudRepository<PlayerEntity, Long> {
}
