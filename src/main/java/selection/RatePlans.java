package selection;

public enum RatePlans {
    ULTRA_COMMUNICATION("ULTRA Communication"),
    SUPER_COMMUNICATION_PLAN("SUPER Communication Plan");

    private String name;

    RatePlans(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
