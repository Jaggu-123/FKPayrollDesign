//package payRoll;

import java.util.*;
import java.time.*;
import java.sql.*;
import java.time.format.*;
import java.io.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

interface UnionInterface {
    double getAmountUnion();
}

interface UnionDuesService {
    String getDueType();
    int setPaid();
}

class LoanDue implements UnionDuesService {

    @Override
    public String getDueType() {
        return "Loan Amount";
    }

    @Override
    public int setPaid() {
        return 1;
    }
}

class MemberShip implements UnionDuesService {

    @Override
    public String getDueType() {
        return "Membership Fee";
    }

    @Override
    public int setPaid() {
        return 1;
    }
}

class UnionDues implements UnionInterface {

    private double amount;
    private int paidOrNot;
    private LocalDate availDate;
    private UnionDuesService unionDuesService;

    UnionDues(double amount, int dueType){
        this.amount = amount;
        this.paidOrNot = 0;
        this.availDate = LocalDate.now();
        if(dueType == 1){ unionDuesService = new LoanDue(); }
    }

    UnionDues(int dueType){
        unionDuesService = new MemberShip();
        this.amount = 255;
        this.availDate = LocalDate.now();
        this.paidOrNot = 0;
    }

    @Override
    public double getAmountUnion() {
        return amount;
    }

    public int getPaidOrNot() {
        return paidOrNot;
    }

    public void setPaidOrNot(){
        this.paidOrNot = unionDuesService.setPaid();
    }

    public UnionDuesService getUnionDuesService(){ return this.unionDuesService; }
    public LocalDate getAvailDate() {return availDate; }
}

interface PayMode {
    String getPaymentMode();
}

class PayModePostal implements PayMode {

    @Override
    public String getPaymentMode() {
        return "Send PayCheck to Postal Adresses";
    }
}

class PayModeByPayMaster implements PayMode {

    @Override
    public String getPaymentMode() {
        return "PickUp by the PayMaster";
    }
}

class PayModeDepositInBank implements PayMode {

    @Override
    public String getPaymentMode() {
        return "Send the Paychecks to Deposit in Bank";
    }
}

interface EmployeeType {
    double calculateSalary(double rate);
}

class HourlyEmployee implements EmployeeType {
    private LocalDate todayDate;
    private int workDuration;

    HourlyEmployee() {
        this.todayDate = LocalDate.now();
        this.workDuration = 0;
    }

    HourlyEmployee(int workDuration){
        this.todayDate = LocalDate.now();
        this.workDuration = workDuration;
    }

    HourlyEmployee(int workDuration, LocalDate date) {
        this.todayDate = date;
        this.workDuration = workDuration;
    }

    public void setWorkDuration(int workDuration){
        todayDate = LocalDate.now();
        this.workDuration = workDuration;
    }

    public LocalDate getTodayDate(){ return this.todayDate; }
    public int getWorkDuration() { return this.workDuration; }

    public double calculateSalary(double rate){
        return workDuration > 8 ? rate*8 + (workDuration-8)*1.5*rate : workDuration*8;
    }
}

class MonthlyEmployee implements EmployeeType {

    public double calculateSalary(double rate){
        return rate;
    }
}

class SalesRecord implements EmployeeType {
    private double amount;
    private LocalDate dateOfSales;

    SalesRecord(double amount) {
        this.amount = amount;
        this.dateOfSales = LocalDate.now();
    }

    public LocalDate getDateOfSales(){ return dateOfSales; }
    public double getAmount() { return amount; }

    @Override
    public double calculateSalary(double rate) {
        return amount*rate;
    }
}

class Employee {
    private int id;
    private String name;
    private LocalDate lastPayRollDate;
    private double salary;
    private double rate;
    private double commissionRate;
    private String type;
    private PayMode employeePayMode;

    Employee(int id, String name, double rate, int type, int payMode){
        this.id = id;
        this.name = name;
        this.lastPayRollDate = LocalDate.now();
        this.salary = 0;
        this.rate = rate;
        this.type = (type == 1 ? "Hourly" : "Monthly");
        this.commissionRate = 0.5;
        if(payMode == 1){ employeePayMode = new PayModePostal(); }
        else if(payMode == 2){ employeePayMode = new PayModeByPayMaster(); }
        else if(payMode == 3){ employeePayMode = new PayModeDepositInBank(); }
    }

    Employee(int id, String name, int payMode){
        this.id = id;
        this.name = name;
        this.lastPayRollDate = LocalDate.now();
        this.salary = 0;
        this.rate = 0;
        this.type = "Temporary";
        this.commissionRate = 0.5;
        if(payMode == 1){ employeePayMode = new PayModePostal(); }
        else if(payMode == 2){ employeePayMode = new PayModeByPayMaster(); }
        else if(payMode == 3){ employeePayMode = new PayModeDepositInBank(); }
    }

    public int getId(){ return id; }
    public String getName() { return name; }
    public LocalDate getLastPayRollDate() { return lastPayRollDate; }
    public double getSalary() { return salary; }
    public double getRate() { return rate; }
    public String getType() { return type; }
    public double getCommissionRate() { return commissionRate; }
    public PayMode getEmployeePayMode() { return employeePayMode; }

    public void setLastPayRollDate(){
        if(type == "Hourly"){
            lastPayRollDate = lastPayRollDate.plusDays(1);
        }
        else{
            lastPayRollDate = lastPayRollDate.plusMonths(1);
        }
    }

    public void setRate(int rate){
        this.rate = rate;
    }
    public void setCommissionRate(int rate) { this.commissionRate = rate; }

    public void setType(String type){
        this.type = type;
    }

}

interface UseCaseOperation {
    void performOperation(Connection con, Scanner in);
}

class AddEmployee implements UseCaseOperation {

    @Override
    public void performOperation(Connection con, Scanner in) {
        System.out.println("Give the Employee Id");
        int employeeId = in.nextInt();
        System.out.println("Give the Employee Name");
        String employeeName = in.next();
        System.out.println("Give the Employee Type");
        System.out.println("\t1. Hourly-Based Employee");
        System.out.println("\t2. Monthly-Based Employee");
        System.out.println("\t3. Temporary Employee");
        int employeeType = in.nextInt();
        double rate = 0;
        if (employeeType == 1 || employeeType == 2) {
            System.out.println("Give the Employee Base Rate");
            rate = in.nextDouble();
        }
        System.out.println("Give the Payment Mode");
        System.out.println("\t1. Postal Address");
        System.out.println("\t2. PickUp by the PayMaster");
        System.out.println("\t3. Send the PayChecks to Deposit in Bank");
        int payType = in.nextInt();

        Employee e = null;
        if(employeeType == 3)
            e = new Employee(employeeId, employeeName, payType);
        else
            e = new Employee(employeeId, employeeName, rate, employeeType, payType);

        try {
            Statement stmt = con.createStatement();
            String payRollDate = e.getLastPayRollDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String sql = "insert into Employee(empId, empName, emplastPayRollDate, empSalary, empRate, empType, empPayMode) values ('" + e.getId() + "','" + e.getName() + "','" + payRollDate + "','" + e.getSalary() + "','" + e.getRate() + "','" + e.getType() + "','" + e.getEmployeePayMode().getPaymentMode() + "')";
            stmt.executeUpdate(sql);
        }catch (SQLException ex){
            System.out.println("Error in Insertiong");
        }
    }
}

class DeleteEmployee implements UseCaseOperation {

    @Override
    public void performOperation(Connection con, Scanner in) {
        System.out.println("Give the Employee Id");
        int employeeId = in.nextInt();

        try{
            Statement stmt = con.createStatement();
            String checkEntrySql = "select * from Employee where empId='" + employeeId + "'";
            ResultSet rs = stmt.executeQuery(checkEntrySql);
            if(!rs.next()) {
                System.out.println("Employee Id " + employeeId + " does not exist in record");
                return;
            } else {
                String sql = "delete from TimeCard where empId='" + employeeId + "'";
                stmt.executeUpdate(sql);
                String sql2 = "delete from Employee where empId='" + employeeId + "'";
                stmt.executeUpdate(sql2);
            }
        } catch (SQLException e) {
            System.out.println("Error in Deletion");
            e.printStackTrace();
        }
    }
}

class PostTimeCard implements UseCaseOperation {

    @Override
    public void performOperation(Connection con, Scanner in) {
        System.out.println("Give The Employee Id");
        int employeeId = in.nextInt();

        try {
            Statement stmt = con.createStatement();
            String checkEntrySql = "select * from Employee where empId='" + employeeId + "' and empType='Hourly'";
            ResultSet rs = stmt.executeQuery(checkEntrySql);
            if(!rs.next()) {
                System.out.println("Employee Id " + employeeId + " does not exist in record");
                return;
            } else{
                System.out.println("Enter the work duration today");
                int workDuration = in.nextInt();
                HourlyEmployee hourlyEmployee = new HourlyEmployee(workDuration);
                String todayDate = hourlyEmployee.getTodayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String sql = "insert into TimeCard(todayDate, workDuration, empId) values ('" + todayDate + "','" + hourlyEmployee.getWorkDuration() + "','" + employeeId + "')";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.out.println("Error in Posting the Time Card");
            e.printStackTrace();
        }
    }
}

class PostSalesCard implements UseCaseOperation {

    @Override
    public void performOperation(Connection con, Scanner in) {
        System.out.println("Give the Employee Id");
        int employeeId = in.nextInt();

        try {
            Statement stmt = con.createStatement();
            String checkEntrySql = "select * from Employee where empId='" + employeeId + "'";
            ResultSet rs = stmt.executeQuery(checkEntrySql);
            if(!rs.next()) {
                System.out.println("Employee Id " + employeeId + " does not exist in record");
                return;
            } else {
                System.out.println("Enter the sales amount");
                double amount = in.nextDouble();
                SalesRecord salesRecord = new SalesRecord(amount);
                String todayDate = salesRecord.getDateOfSales().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String sql = "insert into SalesCard(dateOfSale, amount, empId) values ('" + todayDate + "','" + salesRecord.getAmount() + "','" + employeeId + "')";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            System.out.println("Error in Posting Sales Card");
            e.printStackTrace();
        }
    }
}

class Union implements UseCaseOperation {

    @Override
    public void performOperation(Connection con, Scanner in) {
        System.out.println("1. Avail Union Services");
        System.out.println("2. Take Loan");

        int op = in.nextInt();
        if(op == 1){
            System.out.println("Give the employee Id");
            int employeeId = in.nextInt();

            try {
                Statement stmt = con.createStatement();
                String checkEntry = "select * from UnionMember where empId='" + employeeId + "'";
                ResultSet rs = stmt.executeQuery(checkEntry);
                if(!rs.next()){
                    String insertEntry = "insert into UnionMember(empId) values ('" + employeeId + "')";
                    stmt.executeUpdate(insertEntry);
                    String sql = "select * from UnionMember where empId='" + employeeId + "'";
                    rs = stmt.executeQuery(sql);
                    if(!rs.next()){
                        System.out.println("No Such Employee Exist");
                        return;
                    }
                    else {
                        UnionDues unionDues = new UnionDues(2);
                        String todayDate = unionDues.getAvailDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        String makeDueEntry = "insert into UnionLoan(dateOfLoan, amount, paidOrNot, dueType, unionMemberId) values ('" + todayDate + "','" + unionDues.getAmountUnion() + "','" + unionDues.getPaidOrNot() + "','" + unionDues.getUnionDuesService().getDueType() + "','" + Integer.parseInt(rs.getString(1)) + "')";
                        stmt.executeUpdate(makeDueEntry);
                    }
                } else{
                    System.out.println("The Employee is already a Union Member");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(op == 2){
            System.out.println("Give the Employee Id");
            int employeeId = in.nextInt();

            try{
                Statement stmt = con.createStatement();
                String checkEntry = "select * from UnionMember where empId='" + employeeId + "'";
                ResultSet rs = stmt.executeQuery(checkEntry);
                if(!rs.next()){
                    System.out.println("Emloyee Id " + employeeId + " is not member of Union");
                    return;
                }
                else{
                    System.out.println("Enter the Loan Amount");
                    double amount = in.nextDouble();
                    UnionDues unionDues = new UnionDues(amount,1);
                    String todayDate = unionDues.getAvailDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String makeDueEntry = "insert into UnionLoan(dateOfLoan, amount, paidOrNot, dueType, unionMemberId) values ('" + todayDate + "','" + unionDues.getAmountUnion() + "','" + unionDues.getPaidOrNot() + "','" + unionDues.getUnionDuesService().getDueType() + "','" + Integer.parseInt(rs.getString(1)) + "')";
                    stmt.executeUpdate(makeDueEntry);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

//class EmployeeCollection {
//    private ArrayList<Employee> employeeArrayList;
//
//    EmployeeCollection() {
//        employeeArrayList = new ArrayList<>();
//    }
//
//    public void addEmployee(Employee e) {
//        employeeArrayList.add(e);
//    }
//}

public class Main {

    public static void performDataBaseOperation(UseCaseOperation e, Connection con, Scanner in){
        e.performOperation(con, in);
    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Connection con = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PayRollSystem?autoReconnect=true&useSSL=false", "debian-sys-maint", "TsFGpziP0rsLMWE5");
            System.out.println("Welcome to Flipkart Employee PayRoll System");
            System.out.println("Press the type of Operation");
            System.out.println("1. Add an Employee");
            System.out.println("2. Delete an Employee");
            System.out.println("3. Post a Time Card");
            System.out.println("4. Posting a Sales Card");
            System.out.println("5. Union Service Operation");

            int operationType;
            operationType = in.nextInt();
            if(operationType == 1) {
                performDataBaseOperation(new AddEmployee() ,con, in);
            } else if(operationType == 2){
                performDataBaseOperation(new DeleteEmployee(), con, in);
            } else if(operationType == 3) {
                performDataBaseOperation(new PostTimeCard(), con, in);
            } else if(operationType == 4) {
                performDataBaseOperation(new PostSalesCard(), con, in);
            } else if(operationType == 5) {
                performDataBaseOperation(new Union(), con, in);
            }
        }
        catch(Exception e){
            System.out.println("Error in making Connection with database");
        }
    }
}