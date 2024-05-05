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
        // 각 이메일에 대해 새로운 인증 코드를 생성.
        String ePw = createKey();

        // 이메일을 생성하
        MimeMessage message = createMessage(to, ePw);
        emailSender.send(message);

        // 생성된 인증 코드를 반환
        return ePw;
    }

    private MimeMessage createMessage(String to, String ePw) throws Exception {
        log.info("수신자 : " + to);
        log.info("인증번호 : " + ePw);
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); //수신자 지정
        message.setSubject("[Truetree] 이메일 인증번호를 확인해주세요."); //발신 메일제목

        String msgg = "";
        msgg += "CODE : <strong>" + ePw + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("truetree.factchecker@gmail.com", "factchecker"));

        return message;
    }

    // 인증번호 생성기
    public static String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to) throws Exception {
        // TODO: 인증 코드 생성 로직을 여기에서 호출하지 않도록 수정할 것
        String ePw = createKey();

        MimeMessage message = createMessage(to, ePw);
        log.info(String.valueOf(message));

        try { // 예외처리        message.setFrom(new InternetAddress("truetree.factchecker@gmail.com", "factchecker"));
            emailSender.send(message);
            log.info("이메일 전송에 성공");
        } catch (MailException es) {
            es.printStackTrace();
            log.info("이메일 전송에 실패");
            throw new IllegalArgumentException();
        }
        return ePw; // 반환값
    }
}
