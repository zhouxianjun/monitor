package com.all580.monitor.entity;

import com.all580.monitor.Constant;
import com.all580.monitor.annotation.NoticeType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table(name = "tab_alarm_contacts")
public class TabAlarmContacts implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱地址
     */
    @NoticeType(Constant.NoticeType.EMAIL)
    private String email;

    /**
     * 钉钉机器人
     */
    @NoticeType(Constant.NoticeType.DING)
    private String ding;

    /**
     * 微信关注公众号的openid
     */
    @NoticeType(Constant.NoticeType.WX)
    private String openid;

    /**
     * 手机号码
     */
    private String phone;

    private static final long serialVersionUID = 1L;

}