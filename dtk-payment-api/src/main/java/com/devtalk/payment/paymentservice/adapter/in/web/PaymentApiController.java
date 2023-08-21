package com.devtalk.payment.paymentservice.adapter.in.web;

import com.devtalk.payment.global.code.SuccessCode;
import com.devtalk.payment.global.vo.SuccessResponse;
import com.devtalk.payment.paymentservice.adapter.in.web.dto.PaymentOutput;
import com.devtalk.payment.paymentservice.application.port.in.PaymentUseCase;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.devtalk.payment.paymentservice.application.port.in.dto.PaymentReq.*;
import static com.devtalk.payment.paymentservice.application.port.in.dto.PaymentRes.*;

/*
 * 웹 어댑터의 책임
 * - http 요청을 자바 객체로 매핑하기
 * - 권한 검사 (validator에 넘김)
 * - 입력 유효성 검증하기
 * - 입력을 유즈 케이스의 입력 모델로 매핑하기
 * - 유즈 케이스 호출하기
 * - 유즈 케이스의 출력을 http로 매핑하기
 * - http 응답을 반환하기
 * */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/payment")
class PaymentApiController {
    private final PaymentUseCase paymentUseCase;

    // 결제 링크 요청
    @GetMapping("/{consultationId}/link")
    public ResponseEntity<?> getPaymentLink(@PathVariable("consultationId") Long consultationId){
        String paymentLink = paymentUseCase.getPaymentLink(consultationId);

        return SuccessResponse.toResponseEntity(SuccessCode.GET_PAYMENT_LINK_SUCCESS, paymentLink);
    }

    // TEST: 결제 버튼 페이지
    @GetMapping("/{consultationId}")
    public String payment(@PathVariable("consultationId") Long consultationId, Model model){
        String paymentLink = paymentUseCase.getPaymentLink(consultationId);
        model.addAttribute("paymentLink", paymentLink);

        return "payment-button";
    }

    // 결제 flow 2: 결제 버튼을 누르면 requestPayment를 호출한다.
    // requestPayment는 금액 위변조 확인 및 결제 상태 저장을 위해 결제DB에 임시 결제 정보를 저장한다.
    @PostMapping("/{consultationId}")
    public ResponseEntity<?> requestPayment(@PathVariable("consultationId") Long consultationId) {
        // 포트원에 결제 정보 확인 요청
        paymentUseCase.requestPayment(consultationId);

        // 반환 값
        PaymentSearchRes paymentInfo = paymentUseCase.searchPaymentInfo(consultationId);
        PaymentServiceRes paymentServiceRes = new PaymentServiceRes(paymentInfo.getPaymentId(), paymentInfo.getPaymentUid());
        PaymentOutput paymentOutput
                = new PaymentOutput("0300", "결제 성공", paymentServiceRes);
        return ResponseEntity.status(HttpStatus.OK).body(paymentOutput);
    }

    // 결제 flow 3: 결제 검증
    // 포트원에 결제할 정보를 성공적으로 전송했을때 호출되어야한다.
    // case 1 : 금액이 위변조 되었다면 임시 결제 정보를 삭제한다.
    // case 2 : 결제가 정상 처리 되었다면 임시 결제 정보의 결제 상태를 PAID로 변경한다.
    @PostMapping
    public ResponseEntity<IamportResponse<com.siot.IamportRestClient.response.Payment>>
    validationPayment(@RequestBody PaymentCallbackReq request) {
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse =
                paymentUseCase.paymentByCallback(request);
        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return ResponseEntity.status(HttpStatus.OK).body(iamportResponse);
    }

    // 결제 내역 조회
    @GetMapping("/{consultationId}/info")
    public ResponseEntity<?> getPaymentInfo(
            @PathVariable("consultationId") Long consultationId){
        PaymentSearchRes paymentSearchRes = paymentUseCase.searchPaymentInfo(consultationId);

        return SuccessResponse.toResponseEntity(SuccessCode.GET_PAYMENT_INFO_SUCCESS, paymentSearchRes);
    }

    @DeleteMapping("/{paymentId}")
    public String cancelPayment(){
        return "hello";
    }

    // test
    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "fail-payment";
    }
}
