package com.game.exception;

public class PlayerWithThisIdDontExist extends Exception {
    public PlayerWithThisIdDontExist(String message) {
        super(message);
    }
}
