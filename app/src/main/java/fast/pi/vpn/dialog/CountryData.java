package fast.pi.vpn.dialog;

import com.anchorfree.partner.api.data.Country;

/**
 * Data class of number of servers in specific country
 */
public class CountryData {

    private boolean pro = false;
    private Country countryvalue;

    public boolean isPro() {
        return pro;
    }

    public void setPro(boolean pro) {
        this.pro = pro;
    }

    public Country getCountryvalue() {
        return countryvalue;
    }

    public void setCountryvalue(Country countryvalue) {
        this.countryvalue = countryvalue;
    }

}
