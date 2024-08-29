package buiducnhan.hutech.Online_Shopping_Store.viewmodels.user;

import buiducnhan.hutech.Online_Shopping_Store.entities.User;
import lombok.Builder;

@Builder
public record UserGetVm(Long id, String userName, String firstName, String lastName, String email, String phone) {
    public static UserGetVm from (User user){
        return UserGetVm.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
