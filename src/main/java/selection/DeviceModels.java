package selection;

public enum DeviceModels {
    Samsung_Galaxy_S21_Ultra_128_GB_Phantom_Black("Samsung Galaxy S21 Ultra 128GB Phantom Black"),
    iPhone_13_Pro_Max_128GB_Sierra_Blue("iPhone 13 Pro Max 128GB Sierra Blue");

    private String name;

    DeviceModels(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
