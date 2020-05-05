package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.serivce.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        log.info("ItemController createForm");
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        log.info("ItemController create");
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        log.info("ItemController list");
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        log.info("ItemController updateItemForm");

        Book item = (Book) itemService.findOne(itemId); // 예제를 위한 book casting
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * jpa의 merge(병합), dirty-check(변경감지)
     * <p>
     * 변경감지
     * jpa가 엔티티의 변경을 감지해서 알아서 변경된 항목만 업데이트 쿼리로 만들어 수행하고, 엔티티의 변경내용을 영속성 컨텍스트에 반영함.(트랜젝션 안에 있는 경우)
     * <p>
     * 병합
     * 준영속 엔티티 > 영속성 컨텍스트가 관리하지 않는 엔티티, 엔티티 식별자는 가지고 있음. ex)화면에서 넘어온 폼 데이터에 setId를 한 객체 Book
     * 더티체크가 일어나지 않음.
     * id로 찾은 데이터를 param의 내용으로 모두 덮어쓰기함.
     * <p>
     * 데이터를 변경하는 방법
     * 1. 변경감지 기능 이용 >> itemService.updateItem
     * 2. 머지 사용 >> itemService.saveItem
     * <p>
     * 결론
     * 머지는 원하는 속성만 변경하지 않기 때문에 위험, 모든 파라미터에 값이 없으면 null update될 위험이 있음.
     * 더티체크 사용하기 > 아이디로 조회해서 업데이트 할 항목만 set 해서 영속성 관리로 업데이트 하기.
     *
     * @see jpabook.jpashop.repository.ItemRepository save
     * @see ItemService updateItem
     * @see ItemService saveItem
     */
    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form) {
        log.info("ItemController updateItem");

        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    /**
     * 권장코드
     */
    public String updateItem(@ModelAttribute("form") BookForm form) {
        itemService.updateItem(form.getId(), form.getName(), form.getPrice());
        return "redirect:/items";
    }
}
