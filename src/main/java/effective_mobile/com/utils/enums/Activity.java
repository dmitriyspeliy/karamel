package effective_mobile.com.utils.enums;

public enum Activity {
    YES(93, true),
    NO(95, false);

    private final int code;
    private final Boolean activity;

    Activity(int code, Boolean activity) {
        this.code = code;
        this.activity = activity;
    }

    public static Boolean fromValue(int value) {
        for (var act : Activity.values()) {
            if (value == act.code) {
                return act.activity;
            }
        }
        return null;
    }
}
