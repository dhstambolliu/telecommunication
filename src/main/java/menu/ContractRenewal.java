package menu;

import dao.CustomerUpdateDao;
import selection.ContractPeriod;
import selection.DeviceModels;
import selection.RatePlans;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class ContractRenewal implements CustomerUpdateDao {

    private static final String DB_URL = "jdbc:mysql://192.168.56.1:3306/telecommunication";
    private static final String USER = "dhstambolliu";
    private static final String PASSWORD = "dhstambolliu";

    @Override
    public void contractRenewal() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an existing MSISDN");
        long searchForMSISDN = scanner.nextLong();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement searchMSISDN = connection.prepareStatement("select personalID, endsAt, MSISDN, isActive from communicationPlan inner join customer on communicationPlan.ID = customer.ID where MSISDN = ? ;");
            searchMSISDN.setLong(1, searchForMSISDN);
            boolean resultSetOfSearch = searchMSISDN.execute();

            String endsAt = "";
            boolean isActiveCheck = true;
            String searchForPersonalID = "";
            String foundMSISDN = "";

            if (resultSetOfSearch) {
                ResultSet foundCustomerMSISDN = searchMSISDN.getResultSet();
                if (foundCustomerMSISDN.next()) {
                    searchForPersonalID = foundCustomerMSISDN.getString(1);
                    endsAt = foundCustomerMSISDN.getString(2);
                    foundMSISDN = foundCustomerMSISDN.getString(3);
                    isActiveCheck = foundCustomerMSISDN.getBoolean(4);
                }
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formatDateTimeContractRenewal = now.format(formatter);

                if (foundMSISDN.isEmpty()) {
                    System.out.println("You don't have an active contract for MSISDN: " + searchForMSISDN);
                } else if (formatDateTimeContractRenewal.compareTo(endsAt) >= 0 && isActiveCheck) {
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
                        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formatFutureDateTime = futureDate.format(formatter4);

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
                        insertStatementRatePlan.setLong(7, searchForMSISDN);
                        insertStatementRatePlan.setBoolean(8, isActive);
                        insertStatementRatePlan.setString(9, "Contract Renewal");

                        boolean resultSetRatePlan = insertStatementRatePlan.execute();

                        if (resultSetRatePlan == false) {
                            PreparedStatement updatePreparedStatement = connection.prepareStatement("update communicationPlan set isActive = false WHERE endsAt <= ? and MSISDN = ? ; ");
                            updatePreparedStatement.setString(1, formatDateTimeCreatedRatePlan);
                            updatePreparedStatement.setLong(2, searchForMSISDN);
                            boolean resultSetUpdate = updatePreparedStatement.execute();
                        }

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
                    PreparedStatement elseStatement = connection.prepareStatement("select endsAt, MSISDN, isActive from communicationPlan where MSISDN = ? and isActive = true;");
                    elseStatement.setLong(1, searchForMSISDN);
                    boolean resultSetElseStatement = elseStatement.execute();

                    String endsAtAfter = "";

                    if (resultSetElseStatement) {
                        ResultSet foundEndDateAfter = elseStatement.getResultSet();
                        if (foundEndDateAfter.next()) {
                            endsAtAfter = foundEndDateAfter.getString(1);
                        }
                    }
                    System.out.println("Your contract is still active. Your contract ends at " + endsAtAfter);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void findActiveContracts() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter MSISDN to search for results: ");
        long MSISDN;

        while (!scanner.hasNextLong()) scanner.next();
        {
            MSISDN = scanner.nextLong();
        }

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement findCustomerData = connection.prepareStatement("select firstName, lastName, PersonalID, birthdate, cityOfBirth, nationality, " +
                    "MSISDN, contractPeriod, activationReason, isActive, communicationPlan.createdAt, endsAt, deviceName, brand from Customer inner join communicationPlan on Customer.ID = communicationPlan.customerID" +
                    " inner join device on communicationPlan.ID = device.communicationPlanID where MSISDN = ? and isActive = true ;");
            ArrayList<String> showList = new ArrayList<>();
            findCustomerData.setLong(1, MSISDN);
            boolean resultSet = findCustomerData.execute();
            if (resultSet) {
                ResultSet foundCustomerData = findCustomerData.getResultSet();
                while (foundCustomerData.next()) {
                    String firstName = foundCustomerData.getString(1);
                    String lastName = foundCustomerData.getString(2);
                    String personalID = foundCustomerData.getString(3);
                    String birthdate = foundCustomerData.getString(4);
                    String cityOfBirth = foundCustomerData.getString(5);
                    String nationality = foundCustomerData.getString(6);
                    String getMSISDN = foundCustomerData.getString(7);
                    String contractPeriod = foundCustomerData.getString(8);
                    String activationReason = foundCustomerData.getString(9);
                    Boolean isActive = foundCustomerData.getBoolean(10);
                    String createdAt = foundCustomerData.getString(11);
                    String endsAt = foundCustomerData.getString(12);
                    String deviceName = foundCustomerData.getString(13);
                    String brand = foundCustomerData.getString(14);
                    showList.add(firstName);
                    showList.add(lastName);
                    showList.add(personalID);
                    showList.add(birthdate);
                    showList.add(cityOfBirth);
                    showList.add(nationality);
                    showList.add(getMSISDN);
                    showList.add(contractPeriod);
                    showList.add(activationReason);
                    showList.add(String.valueOf(isActive));
                    showList.add(createdAt);
                    showList.add(endsAt);
                    showList.add(deviceName);
                    showList.add(brand);
                    System.out.println("Name: " + showList.get(0) + " " + showList.get(1) +
                            "\nPersonal ID: " + showList.get(2) +
                            "\nBirthdate: " + showList.get(3) +
                            "\nCity Of Birth: " + showList.get(4) +
                            "\nNationality: " + showList.get(5) +
                            "\nMSISDN: " + showList.get(6) +
                            "\nContract Period: " + showList.get(7) + " Months" +
                            "\nActivation Reason: " + showList.get(8) +
                            "\nIs Contract Active? " + showList.get(9) +
                            "\nContract created at : " + showList.get(10) +
                            "\nContract ends at : " + showList.get(11) +
                            "\nDevice Name: " + showList.get(12) +
                            "\nDevice Brand: " + showList.get(13) + "\n"
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
