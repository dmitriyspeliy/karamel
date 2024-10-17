package effective_mobile.com.utils.enums;

import lombok.Getter;

@Getter
public enum CityInfo {


    CHEL("ЧЕЛЯБИНСК", "https://chelyabinsk.exkursiacaramel.ru", 101),
    TYUMEN("ТЮМЕНЬ", "https://tyumen.exkursiacaramel.ru", 113),
    MSKO("МОСКВА", "https://exkursiacaramel.ru", 97),
    MSKM("МОСКВА", "https://msk.exkursiacaramel.ru", 97),
    SAMARA("САМАРА", "https://samara.exkursiacaramel.ru", 99);

    final String name;
    final String hostName;
    final Integer slotCityNum;

    CityInfo(String name, String hostName, Integer slotCityNum) {
        this.name = name;
        this.hostName = hostName;
        this.slotCityNum = slotCityNum;
    }


}
