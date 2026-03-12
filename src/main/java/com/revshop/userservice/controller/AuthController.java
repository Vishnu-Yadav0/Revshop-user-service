package com.revshop.userservice.controller;

import com.revshop.userservice.dto.*;
import com.revshop.userservice.model.Address;
import com.revshop.userservice.model.Buyer;
import com.revshop.userservice.model.Seller;
import com.revshop.userservice.model.User;
import com.revshop.userservice.service.BuyerService;
import com.revshop.userservice.service.OtpService;
import com.revshop.userservice.service.SellerService;
import com.revshop.userservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final BuyerService buyerService;
    private final SellerService sellerService;
    private final OtpService otpService;

    public AuthController(UserService userService,
                          BuyerService buyerService,
                          SellerService sellerService,
                          OtpService otpService) {
        this.userService = userService;
        this.buyerService = buyerService;
        this.sellerService = sellerService;
        this.otpService = otpService;
    }

    @PostMapping("/register/buyer")
    public ResponseEntity<ApiResponse<BuyerDTO>> registerBuyer(@Valid @RequestBody BuyerDTO buyerDTO) {
        log.info("POST /api/auth/register/buyer - email={}", buyerDTO.getEmail());
        User user = buildUserFromBuyerDTO(buyerDTO);
        Buyer buyer = buyerService.registerBuyer(user);
        BuyerDTO responseDTO = convertBuyerToDTO(buyer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Buyer registered successfully", responseDTO));
    }

    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponse<SellerDTO>> registerSeller(@Valid @RequestBody SellerDTO sellerDTO) {
        log.info("POST /api/auth/register/seller - email={}", sellerDTO.getEmail());
        User user = buildUserFromSellerDTO(sellerDTO);
        Seller seller = sellerService.registerSeller(user, sellerDTO.getBusinessName(), sellerDTO.getBusinessDescription(), sellerDTO.getTaxId());
        SellerDTO responseDTO = convertSellerToDTO(seller);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Seller registered successfully", responseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - email={}", request.getEmail());
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", response));
    }

    @PostMapping("/reactivate")
    public ResponseEntity<ApiResponse<AuthResponse>> reactivateAccount(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("POST /api/auth/reactivate - email={}", loginRequest.getEmail());
        AuthResponse response = userService.loginWithReactivation(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>("Account reactivated successfully", response));
    }

    @GetMapping("/security-question")
    public ResponseEntity<ApiResponse<String>> getSecurityQuestion(@RequestParam String email) {
        log.info("GET /api/auth/security-question - email={}", email);
        String question = userService.getSecurityQuestionByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Security question fetched", question));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("POST /api/auth/reset-password - email={}", request.getEmail());
        userService.resetPasswordBySecurity(request.getEmail(), request.getSecurityAnswer(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>("Password reset successful", null));
    }

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestParam String email) {
        log.info("POST /api/auth/otp/send - email={}", email);
        otpService.generateOtp(email);
        return ResponseEntity.ok(new ApiResponse<>("OTP sent successfully", null));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        log.info("POST /api/auth/otp/verify - email={}", email);
        boolean isValid = otpService.verifyOtp(email, otp);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Invalid or expired OTP", false));
        }
        return ResponseEntity.ok(new ApiResponse<>("OTP verified successfully", true));
    }

    @PostMapping("/forgot-password/send-link")
    public ResponseEntity<ApiResponse<String>> sendResetLink(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        log.info("POST /api/auth/forgot-password/send-link - email={}", email);
        userService.generatePasswordResetToken(email);
        return ResponseEntity.ok(new ApiResponse<>("Reset link sent", null));
    }

    @PostMapping("/forgot-password/reset-via-link")
    public ResponseEntity<ApiResponse<String>> resetViaLink(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        log.info("POST /api/auth/forgot-password/reset-via-link");
        userService.resetPasswordWithToken(token, newPassword);
        return ResponseEntity.ok(new ApiResponse<>("Password reset successfully", null));
    }

    private User buildUserFromBuyerDTO(BuyerDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());
        user.setSecurityQuestion(dto.getSecurityQuestion());
        user.setSecurityAnswer(dto.getSecurityAnswer());
        user.setAddresses(mapAddresses(dto.getAddresses(), user));
        return user;
    }

    private User buildUserFromSellerDTO(SellerDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());
        user.setSecurityQuestion(dto.getSecurityQuestion());
        user.setSecurityAnswer(dto.getSecurityAnswer());
        user.setAddresses(mapAddresses(dto.getAddresses(), user));
        return user;
    }

    private BuyerDTO convertBuyerToDTO(Buyer buyer) {
        BuyerDTO dto = new BuyerDTO();
        dto.setUserId(buyer.getUserId());
        dto.setName(buyer.getUser().getName());
        dto.setEmail(buyer.getUser().getEmail());
        dto.setPhone(buyer.getUser().getPhone());
        dto.setAge(buyer.getUser().getAge());
        dto.setRole("BUYER");
        return dto;
    }

    private SellerDTO convertSellerToDTO(Seller seller) {
        SellerDTO dto = new SellerDTO();
        dto.setUserId(seller.getUserId());
        dto.setName(seller.getUser().getName());
        dto.setEmail(seller.getUser().getEmail());
        dto.setPhone(seller.getUser().getPhone());
        dto.setAge(seller.getUser().getAge());
        dto.setRole("SELLER");
        dto.setBusinessName(seller.getBusinessName());
        dto.setBusinessDescription(seller.getBusinessDescription());
        dto.setTaxId(seller.getTaxId());
        return dto;
    }

    private List<Address> mapAddresses(List<AddressDTO> addressDTOs, User user) {
        if (addressDTOs == null) return new ArrayList<>();
        return addressDTOs.stream().map(dto -> {
            Address address = new Address();
            address.setAddressLine(dto.getAddressLine());
            address.setCity(dto.getCity());
            address.setState(dto.getState());
            address.setZipCode(dto.getZipCode());
            address.setCountry(dto.getCountry());
            address.setIsDefault(dto.getIsDefault());
            address.setAddressType(dto.getAddressType());
            address.setUser(user);
            return address;
        }).toList();
    }
}
