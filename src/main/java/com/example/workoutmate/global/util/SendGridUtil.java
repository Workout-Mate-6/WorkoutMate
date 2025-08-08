package com.example.workoutmate.global.util;

import com.example.workoutmate.domain.user.entity.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendGridUtil {

    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from}")
    private String fromEmail;

    @Value("${spring.sendgrid.template-id}")
    private String templateId;

    // 실제로 발송될 인증 메일(내용)을 만듬
    public void sendEmailVerificationCode(User user, String verificationCode) throws IOException {
        Email from = new Email(fromEmail); // 발신자 정보
        Email to = new Email(user.getEmail()); // 수신자 정보
        Mail mail = new Mail();

        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();

        // 템플릿의 {{name}}과 {{code}} 변수에 값 삽입
        personalization.addDynamicTemplateData("name", user.getName());
        personalization.addDynamicTemplateData("code", verificationCode);

        personalization.addTo(to);
        mail.addPersonalization(personalization);

        send(mail);
    }

    // SendGrid API 호출로 메일 실제 발송
    private void send(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        log.info("SendGrid Response Status: {}", response.getStatusCode());
        log.info("SendGrid Response Body: {}", response.getBody());
    }
}
