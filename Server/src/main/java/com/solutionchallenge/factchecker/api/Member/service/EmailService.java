package com.solutionchallenge.factchecker.api.Member.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender emailSender;
    public static final String ePw = createKey();


    public String sendEmailAndGenerateCode(String to) throws Exception {
        String ePw = createKey();

        MimeMessage message = createMessage(to, ePw);
        emailSender.send(message);

        return ePw;
    }

    private MimeMessage createMessage(String to, String ePw) throws Exception {
        log.info("수신자 : " + to);
        log.info("인증번호 : " + ePw);
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("[Truetree] 이메일 인증번호를 확인해주세요.");

        String msgg = "";
        msgg += "CODE : <strong>" + ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("truetree.factchecker@gmail.com", "factchecker"));

        return message;
    }

    public static String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) {
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to) throws Exception {
        String ePw = createKey();

        MimeMessage message = createMessage(to, ePw);
        log.info(String.valueOf(message));

        try {
            emailSender.send(message);
            log.info("이메일 전송에 성공");
        } catch (MailException es) {
            es.printStackTrace();
            log.info("이메일 전송에 실패");
            throw new IllegalArgumentException();
        }
        return ePw;
    }
}
