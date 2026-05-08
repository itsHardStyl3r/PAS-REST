package pl.hardstyl3r.userservice.soap.api;

import pl.hardstyl3r.userservice.domain.User;

public final class SoapMapper {

    private SoapMapper() {
    }

    public static SoapUser fromUser(User user) {
        SoapUser soapUser = new SoapUser();
        soapUser.setId(user.getId());
        soapUser.setUsername(user.getUsername());
        soapUser.setName(user.getName());
        soapUser.setActive(user.isActive());
        soapUser.setRole(user.getRole() == null ? null : user.getRole().name());
        return soapUser;
    }
}
