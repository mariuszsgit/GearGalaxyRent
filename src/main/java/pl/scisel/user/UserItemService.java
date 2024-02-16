package pl.scisel.user;

import org.springframework.stereotype.Service;
import pl.scisel.item.Item;
import pl.scisel.item.ItemRepository;

import java.util.List;

@Service
public class UserItemService {

    public final ItemRepository itemRepository;

    public UserItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
