package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * x to one(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // 임시 api >> 좋지 않은 방법
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 문제1: 무한루프에 빠짐 (json으로 만드는 과정에서 order > member > order 계속 반복됨.
        // 해결1: order, member 둘중한곳에서는 @JsonIgnore를 설정해서 루프를 끊어줘야함.
        // 문제2: fetch = LAZY > 프록시 객체로 member를 감싸고 있어서 타입에러가 남. 추후에 member를 사용할 때 쿼리를 날려서 프록시를 member로 교체함.
        // 해결2-1: LAZY 일때는 json 생성 안하도록 옵션 설정. (Hibernate5Module 디펜던시 추가, 빈 등록 하면 적용됨.)
        // 해결2-2: 원하는 것만 조회(force lazy loading)
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // 해결2-1
            order.getMember().getName(); // force lazy loading (getMember()까지는 조회쿼리 안나감. 필드까지 get해야함.)
            order.getDelivery().getAddress(); // 이 처리가 귀찮아서 LAZY > EAGER로 바꾸면 안됨.
        }
        return all;
    }

    //dto 전환 api
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // 문제: lazy loading으로 인하여 v1, v2 모두 쿼리가 너무 많이 실행됨.
        // n+1: order를 처음 가져옴, + 각 order 마다 회원, 배송을 각각 한번씩 더 조회함. (1 + n(회원) + n(배송))
        // LAZY > EAGER로 변경하는 것은 해결방법이 아님(쿼리 최적화 되지 않음, 예측하지 못한 쿼리가 생성됨.)
        return orderRepository.findAllByString(new OrderSearch())
                .stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //lazy 초기화 (영속성컨텍스트에서 찾은 후 없으면 디비 조회)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //lazy 초기화 (영속성컨텍스트에서 찾은 후 없으면 디비 조회)
        }
    }

    // fetch join >> best practice
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    // fetch join + 쿼리에서 dto 직접 반환 >> best practice
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }
}
