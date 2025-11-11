package org.example.employmanagement.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {

    @NotNull(        message = " ID can not be empty ! ")
    @Size( min = 2 , message = " ID can not be less than 2 characters !")
    private String ID;

    @NotNull(message = " Name can not be empty ! ")
    @Size( min = 4 , message = " Name can not be less than 4 characters !")
    private String name;

    @NotNull(        message = " Email can not be empty ! ")
    @Email(          message = " please enter a valid Email ! ")
    private String email;

    @Pattern(regexp = "^05.*" , message = " Phone number must starts with 05-xxxxxxxx ! ")
    @Size(min = 10 , max = 10,  message = " Phone number must contain 10 digits numbers only ! ")
    private String phoneNumber;

    @NotNull(message = " Age cannot be null ! ")
    @Min(value = 25 , message = " Age must be at least 25 ! ")
    private Integer    age;

    @NotNull(message = "Position cannot be empty!")
    @Pattern(regexp = "supervisor|coordinator" , message = " Position value must be either supervisor or coordinator only ! ")
    private String position;

    @Pattern(regexp = "false", message = " On Leave value must be false ! ")
    private String onLeave ;

    @NotNull(message = " Hiring Date can not be null  !")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = " Hiring Date con not be in the future ! ")
    private LocalDate hireDate;

    @NotNull(message = " Annual leave value can not be null ! ")
    @Min(value = 0, message = " Annual Leave value can not be less than zero ! ")
    private Integer annualLeave;



}
