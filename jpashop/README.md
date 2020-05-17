# JPA Playground

## 중요

### dto, entity

**엔티티를 dto로 같이 사용하면 절대 안됨.**

* 엔티티를 api로 노출시키면 추후 유지보수에 큰 문제가 생길 수 있음.
* 꼭 dto 로 감싸서 노출해야함.

## tip

### devtools

스프링 부트에서 개발 시 필요한 옵션, 유틸들 추가해주는 라이브러리 (resources 재시작 안해도 적용, 캐시 삭제 등)
* 아래 의존성을 추가하고, 해당 리소스 파일만 recompile 하면 적용됨. 

```groovy
implementation 'org.springframework.boot:spring-boot-devtools'
```

### 쿼리 파라미터 로그 남기기

물음표로 파라미터가 표기되는 대신 쿼리 파라미터 로그로 남길 수 있음.

```yaml
logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.type: trace
```

풀쿼리까지 보고 싶은 경우 외부 라이브러리 사용. [github](https://github.com/gavlyukovskiy/spring-boot-data-source-decorator)
* 개발환경에서만 쓰는걸 권장

```groovy
implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.6.1")
```

### 단축키

#### 파라미터로 변수 빼기 

변수 선택 > `command + option + p` 하면 해당 값이 메서드 파라미터로 빠진다.

```java
class test{
    private Item createItem(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return item;
    }
    
    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}
```

#### 최근 사용 파일

editor > `command + e`

#### upper case

select char > `command + shift + u`

#### Column Selection mode

editor > `command + shift + 8`

* 컬럼 셀렉션 모드를 킨 상태에서 shift + 방향키로 선택을하면 블록지정이 아닌, 열단위로 커서가 선택됨.