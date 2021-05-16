package com.game.controller;

import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.*;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public ResponseEntity getPlayersList(@RequestParam(required = false) String name
            , @RequestParam(required = false) String title
            , @RequestParam(required = false) Race race
            , @RequestParam(required = false) Profession profession
            , @RequestParam(required = false) Long after
            , @RequestParam(required = false) Long before
            , @RequestParam(required = false) Boolean banned
            , @RequestParam(required = false) Integer minExperience
            , @RequestParam(required = false) Integer maxExperience
            , @RequestParam(required = false) Integer minLevel
            , @RequestParam(required = false) Integer maxLevel
            , @RequestParam(required = false) PlayerOrder order
            , @RequestParam(required = false) Integer pageNumber
            , @RequestParam(required = false) Integer pageSize
    ) {
        try {
            return new ResponseEntity(playerService.getPlayerList(name
                    , title
                    , race
                    , profession
                    , after
                    , before
                    , banned
                    , minExperience
                    , maxExperience
                    , minLevel
                    , maxLevel
                    , order
                    , pageNumber
                    , pageSize) ,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при получении списка игроков");
        }
    }

    @GetMapping("/players/count")
    public ResponseEntity getPlayersCount(@RequestParam(required = false) String name
            , @RequestParam(required = false) String title
            , @RequestParam(required = false) Race race
            , @RequestParam(required = false) Profession profession
            , @RequestParam(required = false) Long after
            , @RequestParam(required = false) Long before
            , @RequestParam(required = false) Boolean banned
            , @RequestParam(required = false) Integer minExperience
            , @RequestParam(required = false) Integer maxExperience
            , @RequestParam(required = false) Integer minLevel
            , @RequestParam(required = false) Integer maxLevel
    ) {
        try {
            return new ResponseEntity(playerService.getPlayersCount(name
                    , title
                    , race
                    , profession
                    , after
                    , before
                    , banned
                    , minExperience
                    , maxExperience
                    , minLevel
                    , maxLevel
                    ) ,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при получении Количества игроков в списке");
        }
    }

    @PostMapping("/players")
    public ResponseEntity createPlayer(@RequestBody PlayerEntity player) {
        try {
            return new ResponseEntity(playerService.createPlayer(player), HttpStatus.OK);
        } catch (WrongParamsForCreatePlayer wrongParamsForCreatePlayer) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (EmptyParamsForCreatePlayer emptyParamsForCreatePlayer) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/players/{id}")
    public ResponseEntity getPlayer(@PathVariable Long id) {
        try {
            return new ResponseEntity(playerService.getPlayer(id), HttpStatus.OK);
        } catch (PlayerWithThisIdDontExist playerWithThisIdDontExist) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (NotValidId notValidId) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/players/{id}")
    public ResponseEntity updatePlayer(@PathVariable Long id, @RequestBody PlayerEntity player) {
        try {
            playerService.updatePlayer(id, player);
            return new ResponseEntity(playerService.updatePlayer(id, player), HttpStatus.OK);
        } catch (NotValidId notValidId) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (PlayerWithThisIdDontExist playerWithThisIdDontExist) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (WrongParamsForUpdatePlayer wrongParamsForUpdatePlayer) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (PlayerWithThisIdDontExist playerWithThisIdDontExist) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (NotValidId notValidId) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
