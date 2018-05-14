package com.all580.monitor.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Table(name = "tab_alarm_contacts_relation")
public class TabAlarmContactsRelation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 组ID
     */
    @Column(name = "group_id")
    private Integer groupId;

    /**
     * 联系人ID
     */
    @Column(name = "contacts_id")
    private Integer contactsId;

    private static final long serialVersionUID = 1L;

}