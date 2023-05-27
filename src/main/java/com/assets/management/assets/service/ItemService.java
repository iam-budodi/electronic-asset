package com.assets.management.assets.service;

import com.assets.management.assets.model.entity.Item;
import com.assets.management.assets.model.entity.Supplier;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ItemService {

    @Inject
    Logger LOG;


    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Item> getAllItems(Integer page, Integer size) {
        return Item.find("ORDER BY itemName, qtyBought")
                .page(page, size)
                .list();
    }

    public Item addItem(@Valid Item item) {
        LOG.info("It got here...");
        return Supplier.findByIdOptional(item.supplier.id)
                .map(supplier -> {
                            Item.persist(item);
                            return item;
                        }
                ).orElseThrow(() ->
                        new NotFoundException("Supplier dont exist"));
    }

    public void updateItem(@Valid Item item, @NotNull Long itemId) {
        Item.findByIdOptional(itemId).map(
                itemFound -> Panache.getEntityManager().merge(item)
        ).orElseThrow(
                () -> new NotFoundException("Item dont exist"));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PanacheEntityBase> countItemPerSupplier() {
        return Item
                .find(
                        "SELECT i.supplier.title, "
                                + "COUNT(i.supplier) AS total "
                                + "FROM Item i "
                                + "GROUP BY i.supplier.title"
                )
                .list();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PanacheEntityBase> countItemPerStatus() {
        return Item
                .find(
                        "SELECT i.status, COUNT(i.status) AS total "
                                + "FROM Item i "
                                + "GROUP BY i.status"
                )
                .list();
    }

    public void deleteById(@NotNull Long itemId) {
        Panache.getEntityManager()
                .getReference(Item.class, itemId)
                .delete();
    }

}
