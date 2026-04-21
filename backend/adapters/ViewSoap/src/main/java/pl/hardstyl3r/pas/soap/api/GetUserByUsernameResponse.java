package pl.hardstyl3r.pas.soap.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetUserByUsernameResponse", namespace = "http://p.lodz.pl/pas/soap")
public class GetUserByUsernameResponse {
    private SoapUser user;

    public SoapUser getUser() {
        return user;
    }

    public void setUser(SoapUser user) {
        this.user = user;
    }
}

