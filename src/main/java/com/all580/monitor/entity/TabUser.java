package com.all580.monitor.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "tab_user")
public class TabUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    private static final long serialVersionUID = 1L;

}
