package menu;

import dao.CustomerDao;
import selection.ContractPeriod;
import selection.DeviceModels;
import selection.RatePlans;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class WelcomeCustomer implements CustomerDao {
    private static final String DB_URL = "jdbc:mysql://192.168.56.1:3306/telecommunication";
    private static final String USER = "dhstambolliu";
    private static final String PASSWORD = "dhstambolliu";

    @Override
    public void searchCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an existing personal ID");
        String searchForPersonalID = scanner.nextLine();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement searchCustomer = connection.prepareStatement("select personalID, firstName from customer where personalID = ? ;");
            searchCustomer.setString(1, searchForPersonalID);
            boolean resultSetOfSearch = searchCustomer.execute();

            String personalIDFound = "";
            String firstNameFound = "";
            if (resultSetOfSearch) {
                ResultSet foundCustomerID = searchCustomer.getResultSet();
                if (foundCustomerID.next()) {
                    personalIDFound = foundCustomerID.getString(1);
                    firstNameFound = foundCustomerID.getString(2);
                }
                if (personalIDFound.equals(searchForPersonalID)) {
                    System.out.println("Welcome " + firstNameFound + "\n");
                    try {
                        System.out.println("Select Rate Plan\n");
                        System.out.println("1: " + RatePlans.ULTRA_COMMUNICATION.getName());
                        System.out.println("2: " + RatePlans.SUPER_COMMUNICATION_PLAN.getName());
                        String ratePlanName = "";
                        int price = 0;
                        int selectRatePlan;

                        boolean isRatePlanInputCorrect = false;
                        while (!isRatePlanInputCorrect) {
                            while (!scanner.hasNextInt()) scanner.next();
                            {
                                selectRatePlan = scanner.nextInt();
                            }
                            switch (selectRatePlan) {
                                case 1:
                                    RatePlans.ULTRA_COMMUNICATION.getName();
                                    ratePlanName = RatePlans.ULTRA_COMMUNICATION.getName();
                                    price = 2400;
                                    isRatePlanInputCorrect = true;
                                    break;
                                case 2:
                                    RatePlans.SUPER_COMMUNICATION_PLAN.getName();
                                    ratePlanName = RatePlans.SUPER_COMMUNICATION_PLAN.getName();
                                    price = 1800;
                                    isRatePlanInputCorrect = true;
                                    break;
                                default:
                                    System.out.println("Invalid selection, please enter one of the numbers in the list!");
                                    isRatePlanInputCorrect = false;
                                    break;
                            }
                        }

                        scanner.nextLine();

                        System.out.println("Select Contract Period\n");
                        System.out.println("12: " + ContractPeriod.CONTRACT_PERIOD_12.getName());
                        System.out.println("24: " + ContractPeriod.CONTRACT_PERIOD_24.getName());
                        String contractPeriod = "";
                        int contractPeriodSelection = 0;

                        boolean isContractPeriodInputCorrect = false;

                        while (!isContractPeriodInputCorrect) {
                            while (!scanner.hasNextInt()) scanner.next();
                            {
                                contractPeriodSelection = scanner.nextInt();
                            }
                            switch (contractPeriodSelection) {
                                case 12:
                                    contractPeriod = ContractPeriod.CONTRACT_PERIOD_12.getName();
                                    isContractPeriodInputCorrect = true;
                                    break;
                                case 24:
                                    contractPeriod = ContractPeriod.CONTRACT_PERIOD_24.getName();
                                    isContractPeriodInputCorrect = true;
                                    break;
                                default:
                                    System.out.println("Invalid value, please enter period 12 or 24!");
                                    isContractPeriodInputCorrect = false;
                                    break;
                            }
                        }

                        System.out.println("Enter MSISDN");
                        Long MSISDN = 0L;

                        boolean isMSISDNRegistered = false;
                        try {
                            while (!isMSISDNRegistered) {
                                while (!scanner.hasNextLong()) scanner.next();
                                {
                                    MSISDN = scanner.nextLong();
                                }
                                PreparedStatement preparedStatementToFindIfMSISDNExists = connection.prepareStatement("SELECT MSISDN FROM communicationPlan WHERE MSISDN = ? ;");
                                preparedStatementToFindIfMSISDNExists.setLong(1, MSISDN);
                                boolean resultIfMSISDNExists = preparedStatementToFindIfMSISDNExists.execute();
                                Long checkIfMSISDNWasRegistered = 0L;
                                if (resultIfMSISDNExists) {
                                    ResultSet ifMSISDNFound = preparedStatementToFindIfMSISDNExists.getResultSet();
                                    if (ifMSISDNFound.next()) {
                                        checkIfMSISDNWasRegistered = ifMSISDNFound.getLong(1);
                                    }
                                }
                                if (checkIfMSISDNWasRegistered.equals(MSISDN)) {
                                    System.out.println("MSISDN " + MSISDN + " is already registered, please enter a new MSISDN");
                                    isMSISDNRegistered = false;
                                } else {
                                    isMSISDNRegistered = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID FROM Customer WHERE personalID = ? ;");
                        preparedStatement.setString(1, searchForPersonalID);
                        boolean resultSet2 = preparedStatement.execute();
                        String finalIDFound = "";
                        if (resultSet2) {
                            ResultSet foundID = preparedStatement.getResultSet();
                            if (foundID.next()) {
                                finalIDFound = foundID.getString(1);
                            }
                        }


                        LocalDateTime futureDate = LocalDateTime.now().plusMonths(contractPeriodSelection);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatFutureDateTime = futureDate.format(formatter);

                        LocalDateTime createdAtRatePan = LocalDateTime.now();
                        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatDateTimeCreatedRatePlan = createdAtRatePan.format(formatter2);
                        boolean isActive = true;

                        PreparedStatement insertStatementRatePlan = connection.prepareStatement("insert into CommunicationPlan(contractPeriod, price, ratePlanName, customerID, createdAt, endsAt, MSISDN, isActive, activationReason) " +
                                "values(?,?,?,?,?,?,?,?,?) ;");
                        insertStatementRatePlan.setString(1, contractPeriod);
                        insertStatementRatePlan.setInt(2, price);
                        insertStatementRatePlan.setString(3, ratePlanName);
                        insertStatementRatePlan.setString(4, finalIDFound);
                        insertStatementRatePlan.setString(5, formatDateTimeCreatedRatePlan);
                        insertStatementRatePlan.setString(6, formatFutureDateTime);
                        insertStatementRatePlan.setLong(7, MSISDN);
                        insertStatementRatePlan.setBoolean(8, isActive);
                        insertStatementRatePlan.setString(9, "New Activation");

                        boolean resultSetRatePlan = insertStatementRatePlan.execute();

                        System.out.println("Select Device\n");
                        System.out.println("1: " + DeviceModels.Samsung_Galaxy_S21_Ultra_128_GB_Phantom_Black.getName());
                        System.out.println("2: " + DeviceModels.iPhone_13_Pro_Max_128GB_Sierra_Blue.getName());
                        String deviceModel = "";
                        String deviceBrand = "";
                        int devicePrice = 0;
                        int subsidy;
                        int deviceSelection;

                        boolean isDeviceSelectedInputCorrect = false;
                        while (!isDeviceSelectedInputCorrect) {
                            while (!scanner.hasNextInt()) scanner.next();
                            {
                                deviceSelection = scanner.nextInt();
                            }
                            switch (deviceSelection) {
                                case 1:
                                    deviceModel = DeviceModels.Samsung_Galaxy_S21_Ultra_128_GB_Phantom_Black.getName();
                                    deviceBrand = "Samsung";
                                    if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("12 Months")) {
                                        subsidy = 7200;
                                        devicePrice = 138900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("24 Months")) {
                                        subsidy = 14400;
                                        devicePrice = 138900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else if (ratePlanName.equals("ULTRA Unlimited 50") && contractPeriod.equals("12 Months")) {
                                        subsidy = 4500;
                                        devicePrice = 138900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else {
                                        subsidy = 9000;
                                        devicePrice = 138900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    }
                                    isDeviceSelectedInputCorrect = true;
                                    break;
                                case 2:
                                    deviceModel = DeviceModels.iPhone_13_Pro_Max_128GB_Sierra_Blue.getName();
                                    deviceBrand = "Apple";
                                    if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("12 Months")) {
                                        subsidy = 7200;
                                        devicePrice = 169900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("24 Months")) {
                                        subsidy = 14400;
                                        devicePrice = 169900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else if (ratePlanName.equals("ULTRA Unlimited 50") && contractPeriod.equals("12 Months")) {
                                        subsidy = 4500;
                                        devicePrice = 169900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    } else {
                                        subsidy = 9000;
                                        devicePrice = 169900 - subsidy;
                                        System.out.println("Total to be paid price is " + devicePrice);
                                    }
                                    isDeviceSelectedInputCorrect = true;
                                    break;
                                default:
                                    System.out.println("Invalid selection, please enter one of the numbers in the list!");
                                    isDeviceSelectedInputCorrect = false;
                                    break;
                            }
                        }


                        PreparedStatement findCommunicationPlanIDForeignKey = connection.prepareStatement("select max(ID) from communicationPlan;");

                        ResultSet resultSet3 = findCommunicationPlanIDForeignKey.executeQuery();
                        int finalCommunicationIDFound = 0;
                        ResultSet foundCommunicationID = findCommunicationPlanIDForeignKey.getResultSet();
                        if (foundCommunicationID.next()) {
                            finalCommunicationIDFound = foundCommunicationID.getInt(1);
                        }

                        LocalDateTime createdAtDevice = LocalDateTime.now();
                        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatDateTimeCreatedDevice = createdAtDevice.format(formatter3);

                        PreparedStatement insertDeviceStatement = connection.prepareStatement("insert into device(brand, deviceName, price, communicationPlanID, createdAt) " +
                                "values(?,?,?,?,?) ;");

                        insertDeviceStatement.setString(1, deviceBrand);
                        insertDeviceStatement.setString(2, deviceModel);
                        insertDeviceStatement.setInt(3, devicePrice);
                        insertDeviceStatement.setInt(4, finalCommunicationIDFound);
                        insertDeviceStatement.setString(5, formatDateTimeCreatedDevice);


                        boolean deviceResultSet = insertDeviceStatement.execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    activateContract();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateContract() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name");
        String firstName = scanner.nextLine();
        System.out.println("Enter last name");
        String lastName = scanner.nextLine();
        System.out.println("Enter personal ID");
        String personalID = "";
        boolean isIdRegistered = false;
        try {
            while (!isIdRegistered) {
                personalID = scanner.nextLine();
                Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement preparedStatementToFindIfIDExists = connection.prepareStatement("SELECT PersonalID FROM Customer WHERE PersonalID = ? ;");
                preparedStatementToFindIfIDExists.setString(1, personalID);
                boolean resultIfIdExists = preparedStatementToFindIfIDExists.execute();
                String checkIfIdWasRegistered = "";
                if (resultIfIdExists) {
                    ResultSet ifIdFound = preparedStatementToFindIfIDExists.getResultSet();
                    if (ifIdFound.next()) {
                        checkIfIdWasRegistered = ifIdFound.getString(1);
                    }
                }
                if (checkIfIdWasRegistered.matches(personalID)) {
                    System.out.println("Personal ID " + personalID + " is already registered, please enter a new ID");
                    isIdRegistered = false;
                } else {
                    isIdRegistered = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Enter birthdate. Needed format is yyyy-mm-dd");
        String birthdate = scanner.nextLine();
        System.out.println("Enter city of birth");
        String cityOfBirth = scanner.nextLine();
        System.out.println("Enter nationality");
        String nationality = scanner.nextLine();
        try {
            LocalDateTime createdAtCustomer = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTimeCreatedCustomer = createdAtCustomer.format(formatter);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement insertStatement = connection.prepareStatement("insert into Customer(firstName, lastName, personalID, birthdate, cityOfBirth, nationality, createdAt) " +
                    "values(?,?,?,?,?,?,?) ;");
            insertStatement.setString(1, firstName);
            insertStatement.setString(2, lastName);
            insertStatement.setString(3, personalID.toUpperCase(Locale.ROOT));
            insertStatement.setString(4, birthdate);
            insertStatement.setString(5, cityOfBirth);
            insertStatement.setString(6, nationality);
            insertStatement.setString(7, formatDateTimeCreatedCustomer);

            boolean resultSet = insertStatement.execute();

            if (resultSet = true) {
                System.out.println("Customer data are saved successfully\n");
                try {
                    System.out.println("Select Rate Plan");
                    System.out.println("1: " + RatePlans.ULTRA_COMMUNICATION.getName());
                    System.out.println("2: " + RatePlans.SUPER_COMMUNICATION_PLAN.getName());
                    String ratePlanName = "";
                    int price = 0;
                    int selectRatePlan;

                    boolean isRatePlanInputCorrect = false;

                    while (!isRatePlanInputCorrect) {
                        while (!scanner.hasNextInt()) scanner.next();
                        {
                            selectRatePlan = scanner.nextInt();
                        }
                        switch (selectRatePlan) {
                            case 1:
                                RatePlans.ULTRA_COMMUNICATION.getName();
                                ratePlanName = RatePlans.ULTRA_COMMUNICATION.getName();
                                price = 2400;
                                isRatePlanInputCorrect = true;
                                break;
                            case 2:
                                RatePlans.SUPER_COMMUNICATION_PLAN.getName();
                                ratePlanName = RatePlans.SUPER_COMMUNICATION_PLAN.getName();
                                price = 1800;
                                isRatePlanInputCorrect = true;
                                break;
                            default:
                                System.out.println("Invalid selection, please enter one of the numbers in the list!");
                                isRatePlanInputCorrect = false;
                                break;
                        }
                    }

                    scanner.nextLine();

                    System.out.println("Select Contract Period");
                    System.out.println("12: " + ContractPeriod.CONTRACT_PERIOD_12.getName());
                    System.out.println("24: " + ContractPeriod.CONTRACT_PERIOD_24.getName());
                    String contractPeriod = "";
                    int contractPeriodSelection = 0;

                    boolean isContractPeriodInputCorrect = false;

                    while (!isContractPeriodInputCorrect) {
                        while (!scanner.hasNextInt()) scanner.next();
                        {
                            contractPeriodSelection = scanner.nextInt();
                        }
                        switch (contractPeriodSelection) {
                            case 12:
                                contractPeriod = ContractPeriod.CONTRACT_PERIOD_12.getName();
                                isContractPeriodInputCorrect = true;
                                break;
                            case 24:
                                contractPeriod = ContractPeriod.CONTRACT_PERIOD_24.getName();
                                isContractPeriodInputCorrect = true;
                                break;
                            default:
                                System.out.println("Invalid value, please enter period 12 or 24!");
                                isContractPeriodInputCorrect = false;
                                break;
                        }
                    }


                    System.out.println("Insert MSISDN");
                    Long MSISDN = 0L;

                    boolean isMSISDNRegistered = false;
                    try {
                        while (!isMSISDNRegistered) {
                            while (!scanner.hasNextLong()) scanner.next();
                            {
                                MSISDN = scanner.nextLong();
                            }
                            PreparedStatement preparedStatementToFindIfMSISDNExists = connection.prepareStatement("SELECT MSISDN FROM communicationPlan WHERE MSISDN = ? ;");
                            preparedStatementToFindIfMSISDNExists.setLong(1, MSISDN);
                            boolean resultIfMSISDNExists = preparedStatementToFindIfMSISDNExists.execute();
                            Long checkIfMSISDNWasRegistered = 0L;
                            if (resultIfMSISDNExists) {
                                ResultSet ifMSISDNFound = preparedStatementToFindIfMSISDNExists.getResultSet();
                                if (ifMSISDNFound.next()) {
                                    checkIfMSISDNWasRegistered = ifMSISDNFound.getLong(1);
                                }
                            }
                            if (checkIfMSISDNWasRegistered.equals(MSISDN)) {
                                System.out.println("MSISDN " + MSISDN + " is already registered, please enter a new MSISDN");
                                isMSISDNRegistered = false;
                            } else {
                                isMSISDNRegistered = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID FROM Customer WHERE personalID = ? ;");
                    preparedStatement.setString(1, personalID);
                    boolean resultSet2 = preparedStatement.execute();
                    String finalIDFound = "";
                    if (resultSet2) {
                        ResultSet foundID = preparedStatement.getResultSet();
                        if (foundID.next()) {
                            finalIDFound = foundID.getString(1);
                        }
                    }
                    LocalDateTime futureDate = LocalDateTime.now().plusMonths(contractPeriodSelection);
                    DateTimeFormatter formatterFutureDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formatFutureDateTime = futureDate.format(formatterFutureDate);

                    LocalDateTime createdAtRatePan = LocalDateTime.now();
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formatDateTimeCreatedRatePlan = createdAtRatePan.format(formatter2);
                    boolean isActive = true;
                    PreparedStatement insertStatementRatePlan = connection.prepareStatement("insert into CommunicationPlan(contractPeriod, price, ratePlanName, customerID, createdAt, endsAt, MSISDN, isActive, activationReason) " +
                            "values(?,?,?,?,?,?,?,?,?) ;");
                    insertStatementRatePlan.setString(1, contractPeriod);
                    insertStatementRatePlan.setInt(2, price);
                    insertStatementRatePlan.setString(3, ratePlanName);
                    insertStatementRatePlan.setString(4, finalIDFound);
                    insertStatementRatePlan.setString(5, formatDateTimeCreatedRatePlan);
                    insertStatementRatePlan.setString(6, formatFutureDateTime);
                    insertStatementRatePlan.setLong(7, MSISDN);
                    insertStatementRatePlan.setBoolean(8, isActive);
                    insertStatementRatePlan.setString(9, "New Activation");


                    boolean resultSetRatePlan = insertStatementRatePlan.execute();

                    System.out.println("Select Device");
                    System.out.println("1: " + DeviceModels.Samsung_Galaxy_S21_Ultra_128_GB_Phantom_Black.getName());
                    System.out.println("2: " + DeviceModels.iPhone_13_Pro_Max_128GB_Sierra_Blue.getName());
                    String deviceModel = "";
                    String deviceBrand = "";
                    int devicePrice = 0;
                    int subsidy;
                    int deviceSelection;

                    boolean isDeviceSelectedInputCorrect = false;

                    while (!isDeviceSelectedInputCorrect) {
                        while (!scanner.hasNextInt()) scanner.next();
                        {
                            deviceSelection = scanner.nextInt();
                        }
                        switch (deviceSelection) {
                            case 1:
                                deviceModel = DeviceModels.Samsung_Galaxy_S21_Ultra_128_GB_Phantom_Black.getName();
                                deviceBrand = "Samsung";
                                if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("12 Months")) {
                                    subsidy = 7200;
                                    devicePrice = 138900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("24 Months")) {
                                    subsidy = 14400;
                                    devicePrice = 138900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else if (ratePlanName.equals("ULTRA Unlimited 50") && contractPeriod.equals("12 Months")) {
                                    subsidy = 4500;
                                    devicePrice = 138900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else {
                                    subsidy = 9000;
                                    devicePrice = 138900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                }
                                isDeviceSelectedInputCorrect = true;
                                break;
                            case 2:
                                deviceModel = DeviceModels.iPhone_13_Pro_Max_128GB_Sierra_Blue.getName();
                                deviceBrand = "Apple";
                                if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("12 Months")) {
                                    subsidy = 7200;
                                    devicePrice = 169900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else if (ratePlanName.equals("ULTRA Unlimited 150") && contractPeriod.equals("24 Months")) {
                                    subsidy = 14400;
                                    devicePrice = 169900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else if (ratePlanName.equals("ULTRA Unlimited 50") && contractPeriod.equals("12 Months")) {
                                    subsidy = 4500;
                                    devicePrice = 169900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                } else {
                                    subsidy = 9000;
                                    devicePrice = 169900 - subsidy;
                                    System.out.println("Total to be paid price is " + devicePrice);
                                }
                                isDeviceSelectedInputCorrect = true;
                                break;
                            default:
                                System.out.println("Invalid value, please enter one of the numbers in the list!");
                                isDeviceSelectedInputCorrect = false;
                                break;
                        }
                    }


                    PreparedStatement findCommunicationPlanIDForeignKey = connection.prepareStatement("select max(ID) from communicationPlan;");

                    ResultSet resultSet3 = findCommunicationPlanIDForeignKey.executeQuery();
                    int finalCommunicationIDFound = 0;
                    ResultSet foundCommunicationID = findCommunicationPlanIDForeignKey.getResultSet();
                    if (foundCommunicationID.next()) {
                        finalCommunicationIDFound = foundCommunicationID.getInt(1);
                    }

                    LocalDateTime createdAtDevice = LocalDateTime.now();
                    DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formatDateTimeCreatedDevice = createdAtDevice.format(formatter3);

                    PreparedStatement insertDeviceStatement = connection.prepareStatement("insert into device(brand, deviceName, price, communicationPlanID, createdAt) " +
                            "values(?,?,?,?,?) ;");
                    insertDeviceStatement.setString(1, deviceBrand);
                    insertDeviceStatement.setString(2, deviceModel);
                    insertDeviceStatement.setInt(3, devicePrice);
                    insertDeviceStatement.setInt(4, finalCommunicationIDFound);
                    insertDeviceStatement.setString(5, formatDateTimeCreatedDevice);

                    boolean deviceResultSet = insertDeviceStatement.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}