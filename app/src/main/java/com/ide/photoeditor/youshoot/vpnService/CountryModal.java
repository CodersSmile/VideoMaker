package com.ide.photoeditor.youshoot.vpnService;

import com.anchorfree.partner.api.data.Country;

public class CountryModal {
    public String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country country;

    public String getPathFlag() {
        return pathFlag;
    }

    public void setPathFlag(String pathFlag) {
        this.pathFlag = pathFlag;
    }

    String pathFlag;
}
