package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @JsonIgnore //todo 삭제해야함(lazy 임시)
    @OneToMany(mappedBy = "member") // orders table에 있는 member필드에 종속된다는 의미, order table이 주인테이블
    private List<Order> orders = new ArrayList<>();
}
