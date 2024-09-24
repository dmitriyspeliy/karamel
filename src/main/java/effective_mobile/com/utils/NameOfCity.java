package effective_mobile.com.utils;

import lombok.Getter;

public enum NameOfCity {

    CHEL("ЧЕЛЯБИНСК"),
    EKB("ЕКАТЕРИНБУРГ"),
    TYUMEN("ТЮМЕНЬ"),
    IZH("ИЖЕВСК"),
    KRAS("КРАСНОЯРСК"),
    NSK("НОВОСИБИРСК"),
    SAMARA("САМАРА"),
    VOLGOGRAD("ВОЛГОГРАД"),
    LIPETSK("ЛИПЕЦК"),
    PERM("ПЕРМЬ"),
    SPB("САНКТ-ПЕТЕРБУРГ"),
    YAR("ЯРОСЛАВЛЬ"),
    MSK("МОСКВА");

    @Getter
    final String name;

    NameOfCity(String name) {
        this.name = name;
    }


}
