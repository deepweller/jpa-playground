package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") //구분컬럼에 들어갈 값
@Getter
@Setter
public class Book extends Item {
    private String author;
    private String isbn;
}
