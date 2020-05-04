package jpabook.jpashop.serivce;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * 비즈니스 로직은 대부분 각각의 엔티티 내부에 있음. 서비스 계층은 엔티티로 위임만 함.
     * 엔티티에 핵심 비즈니스 로직을 넣는 방법 > 도메인 모델 패턴
     *
     * 참고. 일반적으로 내가 쓰는 서비스에서 비즈니스 로직을 수행하는 방법 > 트랜젝션 스크립트 패턴
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem); //todo 예제에서는 orderItem는 하나만 등록함. 여러개 넘기는 기능은 추가하면 됨.

        //주문 저장
        orderRepository.save(order); //delivery, orderItem cascade, cascade는 꼭 주의해서 사용하기 > 종속관계가 명확한 상황에서만 사용
        return order.getId();
    }

    /**
     * 취소
     */
    @Transactional
    public void cancerOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel(); //재고나 거래상태를 수정하는 쿼리를 실행할 필요가 없음, jpa에서 엔티티의 더티체크를 해서 알아서 업데이트 시켜줌.
    }

    /**
     * 검색
     */
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
