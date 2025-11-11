package org.example.employmanagement.Controller;


import jakarta.validation.Valid;
import org.example.employmanagement.ApiResponse.ApiResponse;
import org.example.employmanagement.Model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    ArrayList<Employee> employees = new ArrayList<>();

    //display all
    @GetMapping("/get")
    public ArrayList<Employee> getEmployees (){
        return employees ;
    }

    //add employee to DataBase
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee (@Valid @RequestBody Employee employee, Errors errors){

        if (errors.hasErrors()){

            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));

        }

        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse(" Employee information's has been added successfully ! "));

    }

    //update employee information in the DataBase
    @PutMapping("/update/{index}")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody Employee employee , @PathVariable int index, Errors errors){

        if (errors.hasErrors()){

            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));

        }

        if (index < 0 || index >= employees.size()) {
            return ResponseEntity.status(400).body(new ApiResponse(" Error incorrect index  ! "));
        }

        employees.set(index, employee);
        return ResponseEntity.status(200).body(new ApiResponse(" Employee information's has been updated successfully ! "));

    }

    //delete employee information from the DataBase
    @DeleteMapping("/delete/{employeeID}")
    public ResponseEntity<?> deleteEmployee(@Valid @PathVariable String employeeID){

        boolean checked = false;
        int indexNumber = 0;

        if ( employees.isEmpty() ){
          return ResponseEntity.status(400).body(new ApiResponse(" There is no employees in the DataBase to delete one ! "));
        }

        for (Employee employee : employees){

            if (employee.getID().equalsIgnoreCase(employeeID) ){
                checked = true;
                break;
            }

            indexNumber ++;
        }

        if (!checked ){
            return ResponseEntity.status(400).body(new ApiResponse(" There is no employee with that ID number in the DataBAse to delete him ! "));
        }

        employees.remove(indexNumber);
        return ResponseEntity.status(200).body(new ApiResponse(" Employee information's has been deleted successfully ! "));
    }

    // varify if an employee exist from position
    @GetMapping("/get-by-position/{position}")
    public ResponseEntity<?> checkByPosition(@Valid @PathVariable String position){

        ArrayList <Employee> byPositions = new ArrayList<>();

        if ( !(position.equalsIgnoreCase("supervisor") || position.equalsIgnoreCase("coordinator")) ) {
            return ResponseEntity.status(400).body(new ApiResponse(" Position value must be supervisor or coordinator only ! "));
        }

        for (Employee employee1 : employees){

            if(employee1.getPosition().equalsIgnoreCase(position) ){
                byPositions.add(employee1);
            }

        }

        if (byPositions.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse(" There is no employee with that position ! "));
        }

        return ResponseEntity.status(200).body(byPositions);
    }

    // get employees by age range
    @GetMapping("/get-by-age/{min}/{max}")
    public ResponseEntity<?> getByAgeRange (@Valid @PathVariable int min ,@Valid @PathVariable int max){

        ArrayList<Employee> rangedEmployee = new ArrayList<>();

        if (min < 0 || max < 0 || min > max) {
            return ResponseEntity.status(400).body(new ApiResponse(" Incorrect age range ! "));
        }

        for (Employee employee : employees){

            if ( (employee.getAge() >= min ) && (employee.getAge() <= max) ){
                rangedEmployee.add(employee);
            }

        }

        if(rangedEmployee.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse(" There are not any employee with this age range ! "));
        }

        return ResponseEntity.status(200).body(rangedEmployee);
    }


    // Apply for annual leave
    @PostMapping("/apply-leave/{employeeID}")
    public ResponseEntity<?> applyLeave( @PathVariable String employeeID ){

        // check if he exists by id :

        boolean checked = false;
        int indexNumber = 0;
        Employee targetEmployee = null;

        if ( employees.isEmpty() ){
            return ResponseEntity.status(400).body(new ApiResponse(" There is no employees in the DataBase to check the annual leave ! "));
        }

        for (Employee employee : employees){

            if (employee.getID().equalsIgnoreCase(employeeID) ){
                checked = true;
                targetEmployee = employee;
                break;
            }

            indexNumber += 1;
        }

        if (!checked ){
            return ResponseEntity.status(400).body(new ApiResponse(" There is no employee with that ID number in the DataBAse to delete him ! "));
        }

        // check if employee is he on leave or want a leave

        if (targetEmployee.getOnLeave().equalsIgnoreCase("true")){

            return ResponseEntity.status(400).body(new ApiResponse(" Error Employee is already on a leave ! "));

        }

        // check employee anuualleave remaining

        if (targetEmployee.getAnnualLeave() < 1){

            return ResponseEntity.status(400).body(new ApiResponse(" Error Employee must have one day of annual leave ! "));

        }

        // behaviour

        targetEmployee.setOnLeave("true");
        targetEmployee.setAnnualLeave( targetEmployee.getAnnualLeave() - 1 );

        return ResponseEntity.status(200).body(new ApiResponse(" Employee Has Applied for annual leave successfully ! "));

    }

    // check for employee with no annual leave
    @GetMapping("/get-leave-status/{leaveStatus}")
    public ResponseEntity<?> getByNoLeave(@PathVariable String leaveStatus){

        ArrayList <Employee> EmployeeWithNoLeave = new ArrayList<>();


        if (leaveStatus.equalsIgnoreCase("no leave")){
            for (Employee employee1 :employees){

                if (employee1.getAnnualLeave() <= 0 ){
                    EmployeeWithNoLeave.add(employee1);
                }
            }

            if (EmployeeWithNoLeave.isEmpty()) {
                return ResponseEntity.status(400).body(new ApiResponse(" No employee has no annual leave day "));
            }

            return ResponseEntity.status(200).body(EmployeeWithNoLeave);

        }

        return ResponseEntity.status(400).body(new ApiResponse(" Incorrect leave status  ! "));

    }


    @PutMapping("/promote/{targetEmployeeID}/{promoterEmployeeID}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String targetEmployeeID, @PathVariable String promoterEmployeeID ){

        //check if database is empty
        if (employees.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse(" There are no employees in the database ! "));
        }

        Employee targetEmployee = null;
        Employee promoterEmployee = null;

        // find target employees and promoter
        for (Employee emp : employees) {
            if (emp.getID().equalsIgnoreCase(targetEmployeeID)) {
                targetEmployee = emp;
            }
            if (emp.getID().equalsIgnoreCase(promoterEmployeeID)) {
                promoterEmployee = emp;
            }
        }

        // check if  target employee there
        if (targetEmployee == null) {
            return ResponseEntity.status(400).body(new ApiResponse(" there is no employee  with that id to promote him ! "));
        }

        // check if  promoter supervisor there
        if (promoterEmployee == null) {
            return ResponseEntity.status(400).body(new ApiResponse("  there is no supervisor  with that id to promote a regular employee ! "));
        }

        // check if  promoter supervisor is a supervisor
        if (!promoterEmployee.getPosition().equalsIgnoreCase("supervisor")) {
            return ResponseEntity.status(400).body(new ApiResponse(" Only supervisors can promote others ! "));
        }

        // check if target employee is 30 and above
        if (targetEmployee.getAge() < 30) {
            return ResponseEntity.status(400).body(new ApiResponse(" Employee can not be less than 30 years old ! "));
        }

        // check if  target  employee  must not be on leave
        if (targetEmployee.getOnLeave().equalsIgnoreCase("true")) {
            return ResponseEntity.status(400).body(new ApiResponse(" Employee can not be on a leave ! "));
        }

        //  check if target is not a supervisor
        if (targetEmployee.getPosition().equalsIgnoreCase("supervisor")) {
            return ResponseEntity.status(400).body(new ApiResponse(" Employee is already a supervisor ! "));
        }

        // behaviour
        targetEmployee.setPosition("supervisor");
        return ResponseEntity.status(200).body(new ApiResponse(" Employee has been successfully promoted to supervisor! "));
    }


}
