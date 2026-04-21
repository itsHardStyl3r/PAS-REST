package pl.hardstyl3r.pas.soap.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GetResourcesResponse", namespace = "http://p.lodz.pl/pas/soap")
public class GetResourcesResponse {

    @XmlElement(name = "resource")
    private List<SoapResource> resources = new ArrayList<>();

    public List<SoapResource> getResources() {
        return resources;
    }

    public void setResources(List<SoapResource> resources) {
        this.resources = resources;
    }
}

