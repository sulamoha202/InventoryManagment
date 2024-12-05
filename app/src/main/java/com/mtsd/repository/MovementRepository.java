package com.mtsd.repository;

import com.mtsd.model.Movement;
import com.mtsd.model.Product;

import java.util.Date;
import java.util.List;

public interface MovementRepository {
    boolean insertMovement(Movement movement);
    boolean updateMovement(Movement movement);
    boolean deleteMovement(int movementId);
    Movement getMovementById(int movementId);
    List<Movement> getAllMovements();
    List<Movement> getLastMovements(int rowSize);

}
