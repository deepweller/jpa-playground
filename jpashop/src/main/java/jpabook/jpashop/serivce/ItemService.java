package jpabook.jpashop.serivce;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, String name, int price) {
        //트래잭션 내에서 조회, set해야 영속성컨택스트의 더티체킹 가능
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);

        return findItem;
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

}
