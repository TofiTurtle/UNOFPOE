package org.example.eiscuno.exceptions;

//Excepcion marcada para penalizar el jugador o maquina
public class PenaltyException extends Exception {
    private final String penalizedEntity; //Puede ser "PLAYER" o "MACHINE"

    public PenaltyException(String message, String penalizedEntity) {
        super(message);
        this.penalizedEntity = penalizedEntity;
    }

    public String getPenalizedEntity() {
        return penalizedEntity;
    }
}