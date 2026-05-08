package pl.hardstyl3r.rentservice.soap.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetResourcesResponse", namespace = "http://p.lodz.pl/pas/soap")
public class GetResourcesResponse {
    private List<SoapResource> resources;

    public List<SoapResource> getResources() {
        return resources;
    }

    public void setResources(List<SoapResource> resources) {
        this.resources = resources;
    }
}
