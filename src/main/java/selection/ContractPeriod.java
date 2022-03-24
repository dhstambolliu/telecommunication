package selection;

public enum ContractPeriod {
    CONTRACT_PERIOD_12("12"),
    CONTRACT_PERIOD_24("24");

    private String name;

    ContractPeriod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
