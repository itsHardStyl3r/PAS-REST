package pl.hardstyl3r.pas.soap.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CreateAllocationResponse", namespace = "http://p.lodz.pl/pas/soap")
public class CreateAllocationResponse {
    private SoapAllocation allocation;

    public SoapAllocation getAllocation() {
        return allocation;
    }

    public void setAllocation(SoapAllocation allocation) {
        this.allocation = allocation;
    }
}

