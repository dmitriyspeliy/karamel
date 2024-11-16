package effective_mobile.com.utils.enums;

import lombok.Getter;

@Getter
public enum CityInfo {


    CHEL("ЧЕЛЯБИНСК", "https://chelyabinsk.exkursiacaramel.ru", 101, "C3:EXECUTING",
            "https://карамельнаяфабрика.рф/chel"),
    TYUMEN("ТЮМЕНЬ", "https://tyumen.exkursiacaramel.ru", 113, "C9:EXECUTING",
            "https://карамельнаяфабрика.рф/tumen"),
    MSKM("МОСКВА", "https://msk.exkursiacaramel.ru", 97, "FINAL_INVOICE",
            "https://карамельнаяфабрика.рф"),
    MSKO("МОСКВА КРАСНОГОРСК", "https://exkursiacaramel.ru", 203, "C23:EXECUTING",
            "https://карамельнаяфабрика.рф/msk_odin"),
    SAMARA("САМАРА", "https://samara.exkursiacaramel.ru", 99, "C1:EXECUTING",
            "https://карамельнаяфабрика.рф/samara"),
    KSR("КРАСНОЯРСК", "https://ksr.exkursiacaramel.ru", 109, "C13:EXECUTING",
            "https://карамельнаяфабрика.рф/kras"),
    YAR("ЯРОСЛАВЛЬ", "https://yar.exkursiacaramel.ru", 103, "C5:EXECUTING",
            "https://карамельнаяфабрика.рф/yarosl"),
    KZN("КАЗАНЬ", "https://kzn.exkursiacaramel.ru", 105, "C7:EXECUTING",
            "https://карамельнаяфабрика.рф/kazan"),
    SPB("CАНКТ-ПЕТЕРБУРГ", "https://spb.exkursiacaramel.ru", 201, "C25:EXECUTING",
            "https://карамельнаяфабрика.рф/spb");


    final String name;
    final String hostName;
    final Integer slotCityNum;
    final String successPaymentSection;
    final String linkToSmsAction;

    CityInfo(String name, String hostName, Integer slotCityNum, String successPaymentSection, String linkToSmsAction) {
        this.name = name;
        this.hostName = hostName;
        this.slotCityNum = slotCityNum;
        this.successPaymentSection = successPaymentSection;
        this.linkToSmsAction = linkToSmsAction;
    }

    public static Integer getCityCode(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.slotCityNum;
            }
        }
        return null;
    }

    public static String getCitySection(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.successPaymentSection;
            }
        }
        return null;
    }

    public static String getLinkToSmsAction(String name) {
        for (var city : CityInfo.values()) {
            if (city.name.equals(name)) {
                return city.linkToSmsAction;
            }
        }
        return null;
    }

}
