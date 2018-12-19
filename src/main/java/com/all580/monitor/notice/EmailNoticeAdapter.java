package com.all580.monitor.notice;

import com.all580.monitor.Constant;
import com.all580.monitor.dto.Result;
import com.all580.monitor.entity.TabAlarmHistory;
import com.all580.monitor.entity.TabAlarmRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 邮件通知适配器
 * @date 2018/6/7 9:13
 */
@Component
public class EmailNoticeAdapter extends AbstractNotice implements NoticeAdapter<String> {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public Object run(String target, TabAlarmRule rule, TabAlarmHistory history, Result<String> result) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(target);
        helper.setSubject(title(rule, history, result));
        helper.setText(content(rule, history, result), true);
        mailSender.send(message);
        return "ok";
    }

    @Override
    public int type() {
        return Constant.NoticeType.EMAIL;
    }
}
