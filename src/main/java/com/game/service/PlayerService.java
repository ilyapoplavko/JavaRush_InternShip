package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.PlayerEntity;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.*;
import com.game.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepo playerRepo;

    public List<PlayerEntity> getPlayerList(String name
            , String title
            , Race race
            , Profession profession
            , Long after
            , Long before
            , Boolean banned
            , Integer minExperience
            , Integer maxExperience
            , Integer minLevel
            , Integer maxLevel
            , PlayerOrder order
            , Integer pageNumber
            , Integer pageSize
    ) {
        List<PlayerEntity> filteredList = getFilteredList(name
        , title
        , race
        , profession
        , after
        , before
        , banned
        , minExperience
        , maxExperience
        , minLevel
        , maxLevel);

        if (order != null) {
            if (order == PlayerOrder.ID) {
                filteredList.sort(Comparator.comparing(PlayerEntity::getId));
            }

            if (order == PlayerOrder.NAME) {
                filteredList.sort(Comparator.comparing(PlayerEntity::getName));
            }

            if (order == PlayerOrder.LEVEL) {
                filteredList.sort(Comparator.comparing(PlayerEntity::getLevel));
            }

            if (order == PlayerOrder.EXPERIENCE) {
                filteredList.sort(Comparator.comparing(PlayerEntity::getExperience));
            }

            if (order == PlayerOrder.BIRTHDAY) {
                filteredList.sort(Comparator.comparing(PlayerEntity::getBirthdayTime));
            }
        } else {
            filteredList.sort(Comparator.comparing(PlayerEntity::getId));
        }

        if (pageSize == null) {
            pageSize = 3;
        }

        if (pageNumber == null) {
            pageNumber = 0;
        }

        List<PlayerEntity> result = new ArrayList<>();

        int startNumber = pageNumber * pageSize;
        int finishNumber = (pageNumber + 1) * pageSize - 1;

        for (int i = startNumber; i <= finishNumber; i++) {
            if (filteredList.size() > i) {
                result.add(filteredList.get(i));
            }
        }

        return result;
    }

    public Integer getPlayersCount(String name
            , String title
            , Race race
            , Profession profession
            , Long after
            , Long before
            , Boolean banned
            , Integer minExperience
            , Integer maxExperience
            , Integer minLevel
            , Integer maxLevel
    ) {
        List<PlayerEntity> filteredList = getFilteredList(name
                , title
                , race
                , profession
                , after
                , before
                , banned
                , minExperience
                , maxExperience
                , minLevel
                , maxLevel);
        return filteredList.size();
    }

    public PlayerEntity createPlayer(PlayerEntity player) throws WrongParamsForCreatePlayer, EmptyParamsForCreatePlayer {
        if (player.getName() == null ||
                player.getTitle() == null ||
                player.getName().length() > 11 ||
                player.getTitle().length() > 29 ||
                player.getExperience() < 0 ||
                player.getExperience() > 10000000 ||
                player.getBirthday().getTime() < 0 ||
                player.getBirthday().getTime() < 946674000000L ||
                player.getBirthday().getTime() > 32503669200000L) {
            throw new WrongParamsForCreatePlayer("Заданы неверные параметры");
        }

        if (player.getRace() == null ||
        player.getProfession() == null ||
        player.getBirthday() == null ||
        player.getExperience() == null) {
            throw new EmptyParamsForCreatePlayer("Незаданы некоторые параметры");
        }

        if (player.getBanned() == null) {
            player.setBanned(false);
        }

        player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());

        return playerRepo.save(player);
    }

    public PlayerEntity getPlayer(Long id) throws PlayerWithThisIdDontExist, NotValidId {
        if (id == null || id < 1) {
            throw new NotValidId("Неприемлимое Id");
        }
        if (playerRepo.existsById(id)) {
            return playerRepo.findById(id).get();
        } else {
            throw new PlayerWithThisIdDontExist("Player с таким Id не существует");
        }
    }

    public PlayerEntity updatePlayer(Long id, PlayerEntity player) throws NotValidId, PlayerWithThisIdDontExist, WrongParamsForUpdatePlayer {
        if (id == null || id < 1) {
            throw new NotValidId("Неприемлимое Id");
        }
        if (playerRepo.existsById(id)) {
            PlayerEntity changedPlayer = playerRepo.findById(id).get();

            if (player.getName() != null) {
                changedPlayer.setName(player.getName());
            }
            if (player.getTitle() != null) {
                changedPlayer.setTitle(player.getTitle());
            }
            if (player.getRace() != null) {
                changedPlayer.setRace(player.getRace());
            }
            if (player.getProfession() != null) {
                changedPlayer.setProfession(player.getProfession());
            }
            if (player.getBirthday() != null) {
                if (player.getBirthday().getTime() < 0 ||
                        player.getBirthday().getTime() < 946674000000L ||
                        player.getBirthday().getTime() > 32503669200000L) {
                    throw new WrongParamsForUpdatePlayer("Заданы неверные параметры");
                } else {
                    changedPlayer.setBirthday(player.getBirthday());
                }
            }
            if (player.getBanned() != null) {
                changedPlayer.setBanned(player.getBanned());
            }
            if (player.getExperience() != null) {
                if (player.getExperience() < 0 || player.getExperience() > 10000000) {
                    throw new WrongParamsForUpdatePlayer("Заданы неверные параметры");
                } else {
                    changedPlayer.setExperience(player.getExperience());
                }
            }
            changedPlayer.setLevel((int) ((Math.sqrt(2500 + 200 * changedPlayer.getExperience()) - 50) / 100));
            changedPlayer.setUntilNextLevel(50 * (changedPlayer.getLevel() + 1) * (changedPlayer.getLevel() + 2) - changedPlayer.getExperience());
            playerRepo.save(changedPlayer);
            return changedPlayer;
        } else {
            throw new PlayerWithThisIdDontExist("Player с таким Id не существует");
        }
    }

    public void deletePlayer(Long id) throws PlayerWithThisIdDontExist, NotValidId {
        if (id == null || id < 1) {
            throw new NotValidId("Неприемлимое Id");
        }
        if (playerRepo.existsById(id)) {
            playerRepo.deleteById(id);
        } else {
            throw new PlayerWithThisIdDontExist("Player с таким Id не существует");
        }
    }

    public List<PlayerEntity> getFilteredList(String name
            , String title
            , Race race
            , Profession profession
            , Long after
            , Long before
            , Boolean banned
            , Integer minExperience
            , Integer maxExperience
            , Integer minLevel
            , Integer maxLevel
            ) {
        Iterable<PlayerEntity> listWithAll = playerRepo.findAll();
        List<PlayerEntity> filteredList = new ArrayList<>();
        for (PlayerEntity q : listWithAll) {
            filteredList.add(q);
        }

        if (name == null
        && title == null
        && race == null
        && profession == null
        && after == null
        && before == null
        && banned == null
        && minExperience == null
        && maxExperience == null
        && minLevel == null
        && maxLevel == null
        ) {
            return filteredList;
        }

        for (PlayerEntity e: listWithAll) {

//            boolean needToAdd = true;
//
//            if (!(needToAdd == true && name != null && e.getName().contains(name))) {
//                needToAdd = false;
//            }
//
//            if (needToAdd) {
//                filteredList.add(e);
//            }

            if (filteredList.contains(e) && name != null) {
                if (!e.getName().contains(name)) {
                    filteredList.remove(e);
                }
            }

            if (filteredList.contains(e) && title != null) {
                if (!e.getTitle().contains(title)) {
                    filteredList.remove(e);
                }
            }

            if (filteredList.contains(e) && race != null) {
                if (e.getRace() != race) {
                    filteredList.remove(e);
                }
            }

            if (filteredList.contains(e) && profession != null) {
                if (e.getProfession() != profession) {
                    filteredList.remove(e);
                }
            }

            if (filteredList.contains(e) && after != null) {
                if (e.getBirthdayTime() < after) {
                    filteredList.remove(e);
                } else {
                    if (before != null && e.getBirthdayTime() > before) {
                        filteredList.remove(e);
                    }
                }
            }

            if (filteredList.contains(e) && before != null) {
                if (e.getBirthdayTime() > before) {
                    filteredList.remove(e);
                } else {
                    if (after != null && e.getBirthdayTime() < after) {
                        filteredList.remove(e);
                    }
                }
            }

            if (filteredList.contains(e) && banned != null) {
                if (e.getBanned() != banned) {
                    filteredList.remove(e);
                }
            }

            if (filteredList.contains(e) && minExperience != null) {
                if (e.getExperience() < minExperience) {
                    filteredList.remove(e);
                } else {
                    if (maxExperience != null && e.getExperience() > maxExperience) {
                        filteredList.remove(e);
                    }
                }
            }

            if (filteredList.contains(e) && maxExperience != null) {
                if (e.getExperience() > maxExperience) {
                    filteredList.remove(e);
                } else {
                    if (minExperience != null && e.getExperience() < minExperience) {
                        filteredList.remove(e);
                    }
                }
            }

            if (filteredList.contains(e) && minLevel != null) {
                if (e.getLevel() < minLevel) {
                    filteredList.remove(e);
                } else {
                    if (maxLevel != null && e.getLevel() > maxLevel) {
                        filteredList.remove(e);
                    }
                }
            }

            if (filteredList.contains(e) && maxLevel != null) {
                if (e.getLevel() > maxLevel) {
                    filteredList.remove(e);
                } else {
                    if (minLevel != null && e.getLevel() < minLevel) {
                        filteredList.remove(e);
                    }
                }
            }

        }
        return filteredList;
    }
}
