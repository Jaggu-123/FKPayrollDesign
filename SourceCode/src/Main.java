//package payRoll;

import java.util.*;
import java.time.*;
import java.sql.*;
import java.time.format.*;
import java.io.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

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

class Employee {
    private int id;
    private String name;
    private LocalDate lastPayRollDate;
    private double salary;
    private double rate;
    private String type;
    private EmployeeType employeeType;

    Employee(int id, String name, double rate, String type){
        this.id = id;
        this.name = name;
        this.lastPayRollDate = LocalDate.now();
        this.salary = 0;
        this.rate = rate;
        this.type = type;
        if(this.type.equals("Hourly")){
             this.employeeType = new HourlyEmployee();
        }
        else{
             this.employeeType = new MonthlyEmployee();
        }
    }

    public int getId(){ return id; }
    public String getName() { return name; }
    public LocalDate getLastPayRollDate() { return lastPayRollDate; }
    public double getSalary() { return salary; }
    public double getRate() { return rate; }
    public String getType() { return type; }

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

    public void setType(String type){
        this.type = type;
        if(this.type.equals("Hourly")){
             this.employeeType = new HourlyEmployee();
        }
        else{
             this.employeeType = new MonthlyEmployee();
        }
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
        String employeeType = in.next();
        System.out.println("Give the Employee Base Rate");
        double rate = in.nextDouble();

        Employee e = new Employee(employeeId, employeeName, rate, employeeType);

        try {
            Statement stmt = con.createStatement();
            String payRollDate = e.getLastPayRollDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String sql = "insert into Employee(empId, empName, emplastPayRollDate, empSalary, empRate, empType) values ('" + e.getId() + "','" + e.getName() + "','" + payRollDate + "','" + e.getSalary() + "','" + e.getRate() + "','" + e.getType() + "')";
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

            int operationType;
            operationType = in.nextInt();
            if(operationType == 1) {
                performDataBaseOperation(new AddEmployee() ,con, in);
            } else if(operationType == 2){
                performDataBaseOperation(new DeleteEmployee(), con, in);
            } else if(operationType == 3) {
                performDataBaseOperation(new PostTimeCard(), con, in);
            }
        }
        catch(Exception e){
            System.out.println("Error in making Connection with database");
        }
    }
}