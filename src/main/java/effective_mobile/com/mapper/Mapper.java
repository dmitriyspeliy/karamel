package effective_mobile.com.mapper;

import effective_mobile.com.model.Cart;
import effective_mobile.com.model.dto.rs.GetBookingCartResponse;

public class Mapper {

    public static GetBookingCartResponse toDto(Cart cart) {
        return GetBookingCartResponse.builder()
                .leadId(cart.getLeadId())
                .contactId(cart.getContactId())
                .name(cart.getName())
                .managerContactNumber(cart.getManagerContactNumber())
                .paymentLink(cart.getPaymentLink())
                .kidCount(cart.getKidCount())
                .kidPrice(cart.getKidPrice())
                .adultCount(cart.getAdultCount())
                .adultPrice(cart.getAdultPrice())
                .totalPrice(cart.getTotalPrice())
                .kidAge(cart.getKidAge())
                .address(cart.getAddress())
                .time(cart.getTime())
                .createdAt(cart.getCreatedAt())
                .build();
    }

}
