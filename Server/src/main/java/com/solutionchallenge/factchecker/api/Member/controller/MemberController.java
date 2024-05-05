package com.solutionchallenge.factchecker.api.Member.controller;

import com.solutionchallenge.factchecker.api.Member.dto.request.LoginRequestDto;
import com.solutionchallenge.factchecker.api.Member.dto.request.MailConfirmRequest;
import com.solutionchallenge.factchecker.api.Member.dto.request.SignupRequestDto;
import com.solutionchallenge.factchecker.api.Member.dto.response.MyProfileResponseDto;
import com.solutionchallenge.factchecker.api.Member.dto.response.SignupResponseDto;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.entity.UserDetailsImpl;
import com.solutionchallenge.factchecker.api.Member.repository.MemberRepository;
import com.solutionchallenge.factchecker.api.Member.service.EmailService;
import com.solutionchallenge.factchecker.api.Member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final EmailService mailService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    @Operation(summary = "이메일 인증", description = "이메일 인증번호 (ePw) 를 입력받은 메일로 전송합니다.")
    @PostMapping("/auth/confirm")
    public ResponseEntity emailConfirm(@RequestBody MailConfirmRequest dto) throws Exception {

        String ePw = mailService.sendEmailAndGenerateCode(dto.email);
        Map<String, String> confirmResult = new HashMap<>();
        confirmResult.put("AuthenticationCode", ePw);
        return ResponseEntity.ok(confirmResult);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity signup(HttpServletResponse res, @RequestBody SignupRequestDto requestDto) throws Exception {
        Map<String, String> result = new HashMap<>();

        // 중복가입 방지
        if (memberRepository.existsMemberById(requestDto.getId())) {
            result.put("error", "이미 존재하는 이메일입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
//            return GlobalResponse.conflict("error", "존재하는 이메일입니다.");
        }

        // 닉네임 중복검사
        else if (memberRepository.existsMemberByNickname(requestDto.getNickname())) {
            result.put("error", "이미 존재하는 닉네임입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
//            return GlobalResponse.conflict("error", "중복된 닉네임입니다.");
        }

        else {
            // 회원가입 진행
            Member member = memberService.signup(res, requestDto);
            SignupResponseDto responseDto = SignupResponseDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .grade(requestDto.getGrade())
                    .interests(requestDto.getInterests())
//                   .interests(requestDto.getInterests()) //json 형식
                    .build();
            return ResponseEntity.ok(responseDto);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(HttpServletResponse res, @RequestBody LoginRequestDto dto) throws Exception {
        try {
            return ResponseEntity.ok(memberService.login(res, dto));
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            Map<String, String> result = new HashMap<>();
            result.put("error", "인증 정보가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        MyProfileResponseDto dto = memberService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok().body(dto);
    }
}