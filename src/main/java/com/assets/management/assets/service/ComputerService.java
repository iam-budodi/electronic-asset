package com.assets.management.assets.service;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.Computer;
import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Purchase;
import com.assets.management.assets.model.Supplier;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ComputerService {

}
