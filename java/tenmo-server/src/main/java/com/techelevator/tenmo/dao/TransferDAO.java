package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {

    Transfer makeATransfer(String username, int user_id, BigDecimal amount);
    List<Transfer> transferList(String username);
    Transfer viewTransferById(int transfer_id);
    Transfer requestTransfer(String username, int user_id, BigDecimal amount);
    List<Transfer> pendingTransferList(String username);
    Transfer approveRejectTransfer(int transfer_id, int transfer_status_id, String username);

}
