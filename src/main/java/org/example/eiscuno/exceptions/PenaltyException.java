package org.example.eiscuno.exceptions;

/**
 * Custom exception class for game penalty scenarios.
 * Used to penalize either the player or machine when game rules are violated.
 */
public class PenaltyException extends Exception {
    /**
     * The entity being penalized ("PLAYER" or "MACHINE")
     */
    private final String penalizedEntity;

    /**
     * Constructs a new penalty exception with a message and penalized entity.
     *
     * @param message The detail message explaining the penalty
     * @param penalizedEntity The entity being penalized ("PLAYER" or "MACHINE")
     */
    public PenaltyException(String message, String penalizedEntity) {
        super(message);
        this.penalizedEntity = penalizedEntity;
    }

    /**
     * Gets the entity that was penalized.
     *
     * @return The penalized entity ("PLAYER" or "MACHINE")
     */
    public String getPenalizedEntity() {
        return penalizedEntity;
    }
}