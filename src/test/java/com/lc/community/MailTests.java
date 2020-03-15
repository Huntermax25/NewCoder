package com.lc.community;


import com.lc.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送普通文件
     */
    @Test
    public void testTextMail(){
        mailClient.sendMail("1055465117@qq.com","Test","加油");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","Lc");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1055465117@qq.com","华为校园招聘",content);
    }


}
