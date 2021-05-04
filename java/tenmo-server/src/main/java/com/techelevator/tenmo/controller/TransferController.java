package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
public class TransferController {
    @Autowired
    JdbcTransferDAO transferDAO;

    public TransferController(JdbcTransferDAO transferDAO){
        this.transferDAO = transferDAO;
    }

    @ApiOperation("Feature to transfer TE Bucks to another user")
    @RequestMapping(path = "/maketransfer/{user_id}", method = RequestMethod.POST)
    public Transfer makeATransfer(@RequestBody Transfer transfer, @PathVariable int user_id, Principal principal){
          return transferDAO.makeATransfer(principal.getName(),user_id,transfer.getAmount());
    }

    @ApiOperation("Feature to view all transfers")
    @RequestMapping(path ="/viewtransfers", method = RequestMethod.GET)
    public List<Transfer> transferList(Principal principal) {
    return transferDAO.transferList(principal.getName());
    }

    @ApiOperation("Feature to view pending transfers")
    @RequestMapping(path = "/pendingtransfers", method = RequestMethod.GET)
    public List<Transfer> pendingTransferList(Principal principal) {
        return transferDAO.pendingTransferList(principal.getName());
    }

    @ApiOperation("Feature to view a specific transfer by transfer id")
    @RequestMapping(path = "/transfer/{transfer_id}", method = RequestMethod.GET)
    public Transfer viewTransferById(@PathVariable int transfer_id){
        return transferDAO.viewTransferById(transfer_id);
    }


    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation("This mapping allows users to request money from another user")
    @RequestMapping(path = "/requesttransfer/{user_id}", method = RequestMethod.POST)
    public Transfer requestTransfer(@RequestBody Transfer transfer, @PathVariable int user_id, Principal principal){
        return transferDAO.requestTransfer(principal.getName(),user_id,transfer.getAmount());
    }

   @ApiOperation("This feature allows you to approve or reject a transfer")
    @RequestMapping(path= "/approverejecttransfers", method = RequestMethod.PUT)
    public Transfer approveRejectTransfer(@RequestBody Transfer transfer, Principal principal) {
       return transferDAO.approveRejectTransfer(transfer.getTransfer_id(), transfer.getTransfer_status_id(), principal.getName());
   }
}










