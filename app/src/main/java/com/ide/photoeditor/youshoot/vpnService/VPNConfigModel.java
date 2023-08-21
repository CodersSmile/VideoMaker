package com.ide.photoeditor.youshoot.vpnService;

public class VPNConfigModel {

    private Boolean vpn_enabled =false;
    private ConfigDetailsDTO config_details =new ConfigDetailsDTO();

    public Boolean getVpn_enabled() {
        return vpn_enabled;
    }

    public void setVpn_enabled(Boolean vpn_enabled) {
        this.vpn_enabled = vpn_enabled;
    }

    public ConfigDetailsDTO getConfig_details() {
        return config_details;
    }

    public void setConfig_details(ConfigDetailsDTO config_details) {
        this.config_details = config_details;
    }

    public static class ConfigDetailsDTO {
        private String vpn_key="";
        private String vpn_url="";

        public String getVpn_key() {
            return vpn_key;
        }

        public void setVpn_key(String vpn_key) {
            this.vpn_key = vpn_key;
        }

        public String getVpn_url() {
            return vpn_url;
        }

        public void setVpn_url(String vpn_url) {
            this.vpn_url = vpn_url;
        }
    }
}
