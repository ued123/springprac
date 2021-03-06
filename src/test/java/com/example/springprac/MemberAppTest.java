package com.example.springprac;

import com.example.Impl.FixDiscountPolicy;
import com.example.Impl.MemberServiceImpl;
import com.example.Impl.OrderServiceImpl;
import com.example.config.AppConfig;
import com.example.dto.Member;
import com.example.dto.Order;
import com.example.meta.Grade;
import com.example.role.DiscountPolicy;
import com.example.role.MemberService;
import com.example.role.OrderService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberAppTest {

    MemberService memberService;
    OrderService orderService;
    DiscountPolicy discountPolicy;

    @BeforeEach
    public void testConfig() {

        /*
            생성자 주입(구현체 주입)
         */
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
        discountPolicy = new FixDiscountPolicy();
    }

    @Test
    @DisplayName("멤버 테스트 app 구동 ")
    public void memberAppTest() {

        Member member = new Member(1L,"skydrive860@gmail.com", Grade.normal);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        assertEquals(findMember, member);

    }

    @Test
    @DisplayName("AppConfig 객체(DI 컨테이너)를를 어플리케이션 컨텍스트에 등록 ")
    public void AnnotationConfigContextTest() {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        /*
            스프링 컨텍스트 등록시에 @bean의 메서드 이름으로 등록되어 구현체를 찾을떄 메서드 이름으로 호출 할 수 있다.
         */
        MemberService memberService = applicationContext.getBean("memberService",MemberService.class);

        Member member = new Member(1L,"skydrive860@gmail.com", Grade.normal);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        assertEquals(findMember, member);

    }


    @Test
    @DisplayName("오더 기능 app 구동 Mocking")
    public void OrderAppMocking(@Mock OrderService orderService) {

        memberService.join(new Member(1L,"skydrive860@gmail.com",Grade.vip));

        Member member = memberService.findMember(1L);

        when(orderService.createOrder(member, "itemA", 10000))
                .thenReturn(createOrderTest(member,"itemA",10000));

        Order orderRes = orderService.createOrder(member, "itemA", 10000);

        System.out.println(orderRes.toString());

        System.out.println("testSuccess");

        assertEquals(9000, orderRes.calculateDiscountPrice());

    }


    public Order createOrderTest(Member member, String itemName, int itemPrice) {

        memberService.join(new Member(1L,"skydrive860@gmail.com",Grade.vip));

        Member m = memberService.findMember(1L);

        int fixDiscountPrice = discountPolicy.discountFix(m,itemPrice);

        return new Order(m.getId(), itemName, itemPrice, fixDiscountPrice);
    }
}
