package com.example.Impl;

import com.example.dto.Member;
import com.example.dto.Order;
import com.example.role.DiscountPolicy;
import com.example.role.MemberRepository;
import com.example.role.OrderService;

public class OrderServiceImpl implements OrderService {

    private MemberRepository memberRepository = new MemoryMemberRepositoryImpl();

    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Member member, String itemName, int itemPrice) {

        Member m = memberRepository.findById(member.getId());
        int fixDiscountPrice = discountPolicy.discountFix(m,itemPrice);

        return new Order(m.getId(), itemName, itemPrice, fixDiscountPrice);
    }
}