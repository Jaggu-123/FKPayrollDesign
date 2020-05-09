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

    public void setWorkDuration(int workDuration){
        todayDate = todayDate.plusDays(1);
        this.workDuration = workDuration;
    }

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

    public static void addEmployee(Connection con, Employee e){
        try {
            Statement stmt = con.createStatement();
            String payRollDate = e.getLastPayRollDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String sql = "insert into Employee(empId, empName, emplastPayRollDate, empSalary, empRate, empType) values ('" + e.getId() + "','" + e.getName() + "','" + payRollDate + "','" + e.getSalary() + "','" + e.getRate() + "','" + e.getType() + "')";
            stmt.executeUpdate(sql);
        }catch (SQLException ex){
            System.out.println("Error in Insertiong");
        }
    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Connection con = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PayRollSystem?autoReconnect=true&useSSL=false", "debian-sys-maint", "TsFGpziP0rsLMWE5");
//            con = DriverManager.getConnection("jdbc:mysql:///PayRollSystem", "root", "manish123");
            System.out.println("Welcome to Flipkart Employee PayRoll System");
            System.out.println("Press the type of Operation");
            System.out.println("1. Add an Employee");

            int operationType;
            operationType = in.nextInt();
            if(operationType == 1) {
                System.out.println("Give the Employee Id");
                int employeeId = in.nextInt();
                System.out.println("Give the Employee Name");
                String employeeName = in.next();
                System.out.println("Give the Employee Type");
                String employeeType = in.next();
                System.out.println("Give the Employee Base Rate");
                double rate = in.nextDouble();

                Employee e = new Employee(employeeId, employeeName, rate, employeeType);
                addEmployee(con, e);
//                System.out.println(e.getId() + e.getName() + e.getRate() + e.getSalary() + e.getType());
            }
        }
        catch(Exception e){
            System.out.println("Error in making Connection with database");
        }
    }
}