package com.rutuja.authorization.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "userdetails")
public class ResistrationBean {

    @Column(value = "fname")
    private String fname;

    @Column(value="lname")
    private String lname;

    @Column(value="mob")
    private String mob;

    @Column()
    private String password;

    @Column(value="confirmpassword")
    private String confirmpassword;

    @Id
    @Column(value = "username")
    private String email;

}
