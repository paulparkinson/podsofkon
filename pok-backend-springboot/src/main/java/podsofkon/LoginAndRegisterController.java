package podsofkon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import podsofkon.model.Account;
import podsofkon.model.Usage;
import podsofkon.repository.AccountRepository;
import podsofkon.email.SendMail;
import podsofkon.repository.UsageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginAndRegisterController {

    final AccountRepository accountRepository;
    final UsageRepository usageRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, String> verificationCodeToEmailMap = new HashMap<>();
    public LoginAndRegisterController(AccountRepository accountRepository, UsageRepository usageRepository) {
        this.accountRepository = accountRepository;
        this.usageRepository = usageRepository;
        log.info("accountRepository:" + accountRepository);
        log.info("usageRepository:" + usageRepository);
    }



    @PostMapping("/register")
    @Transactional
    public String register(@RequestParam("firstname") String firstname,
                        @RequestParam("lastname") String lastname,
                        @RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam("passwordconfirm") String passwordconfirm, Model model)  {
        if(!password.equals(passwordconfirm)) return "errorPage"; //todo do this on client side
        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("email", email);
        List<Account> account = accountRepository.findByEmail(email);
        if (account.size() > 0 && account.get(0).getAuthKey() != null) {
            log.error("account already exists for email:" + email);
            return "accountexists";
        }
        accountRepository.saveAndFlush(new Account(firstname, lastname, email, password));
        String verificationKey = "" + new SecureRandom().nextInt(10000)+ new SecureRandom().nextInt(10000);
        verificationCodeToEmailMap.put(verificationKey, email);
        new SendMail().send(email, "Thank you for registering with us at XRCloud Services!" +
                "<br>" +
                "Please click the link below to confirm your registration and get the token required to access your services and thanks again!... " +
                "<br>" +
                "<a href=\"https://asdf.com/confirmregistration?c=" + verificationKey +
                "\">Confirm Registration</a>" +
                "<br>" +
                "<br>XRCloudServices.com");
        //todo queue this...
//        new SendMail().send("paul.parkinson@oracle.com",
//                "New registration email:" + email + " " + " name:" + firstname + " " + lastname);
        return "thanksforregistering";
    }

    @GetMapping("/confirmregistration")
    public String confirmcode(@RequestParam("c") String confirmcode, Model model) {
        if(confirmcode == null || confirmcode.trim().equals("") || verificationCodeToEmailMap.get(confirmcode) == null)
            return "confirmationcodedoesntexist";
        List<Account> accounts = accountRepository.findByEmail(verificationCodeToEmailMap.get(confirmcode));
        if (accounts.size() == 0 || accounts.get(0) == null)
            return "confirmationcodedoesntexist"; //shouldn't happen
        String tokenValue = "" + new SecureRandom().nextInt(10000)+ new SecureRandom().nextInt(10000); //todo generate JWT
        Account account = accounts.get(0);
        log.info("confirm registration of account: " + account.getEmail() +
                " confirmcode:" + confirmcode + " tokenValue:" + tokenValue);
        account.setAuthKey(tokenValue);
        account.setBalance(new BigDecimal(20.00));
        accountRepository.saveAndFlush(account);
        model.addAttribute("token", tokenValue);
        model.addAttribute("email", account.getEmail());
        verificationCodeToEmailMap.remove(confirmcode);
        return "registrationconfirmation";
    }

    @PostMapping("/accountinfo")
    public String accountinfo(@RequestParam("email") String email, @RequestParam("pw") String pw, Model model) {
        List<Account> accounts = accountRepository.findByEmail(email);
        if (accounts.isEmpty() || accounts.isEmpty() || !accounts.get(0).getPassword().equals(pw)) {
            return "loginfailed";
        }
        Account account = accounts.get(0);
        model.addAttribute("account", account);
        List<Usage> usages = usageRepository.findByUsageId_Email(email);
        double accumulatedCost = 0;
        for (Usage usage : usages) {
            usage.setUsageCost(getStringForCost(usage));
            accumulatedCost += getCostForUsage(usage);
        }
        model.addAttribute("usages", usages);
        model.addAttribute("totalcost", df.format(accumulatedCost));
        BigDecimal accumulatedCostBigDecimal = new BigDecimal(accumulatedCost);
        model.addAttribute("balance",
                account.getBalance().subtract(accumulatedCostBigDecimal).setScale(2, RoundingMode.CEILING));
        return "accountinfo";
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private String getStringForCost(Usage usage) {
        double amt = AuthAndUsageTracking.costPerUseForServiceUsageName(usage.getUsageId().usageName) *
                usage.getUsageCount();
//        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return df.format(amt);
    }
    private double getCostForUsage(Usage usage) {
        return AuthAndUsageTracking.costPerUseForServiceUsageName(usage.getUsageId().usageName) *
                usage.getUsageCount();
    }


    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts(@RequestParam("pw") String pw) {
        try {
            if (pw.equals("deleteme")) {
                log.info("deleting accounts...");
                List<Account> accounts = accountRepository.findByEmail("paul.parkinson@oracle.com");
                accountRepository.deleteAll(accounts);
                log.info(accounts.size() + " accounts deleted");
            }
            else if (!pw.equals("1Nasdf")) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            log.info("get all accounts ...");
            List<Account> accounts = new ArrayList<Account>();
            accounts.addAll(accountRepository.findAll());
            if (accounts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/usages")
    public ResponseEntity<List<Usage>> getAllUsages(@RequestParam("pw") String pw) {
        try {
            if (!pw.equals("asdf")) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            log.info("get all usages ...");
            List<Usage> usages = new ArrayList<Usage>();
            usages.addAll(usageRepository.findAll());
            if (usages.isEmpty()) {
                return new ResponseEntity<>(usages, HttpStatus.OK);
            }
            return new ResponseEntity<>(usages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //    @DeleteMapping("/delete")
    @GetMapping("/delete")
    public String deleteAccount(
            @RequestParam("pw") String pw, @RequestParam("email") String email, Model model) {
        try {
            List<Account> accounts = accountRepository.findByEmail(email);
            if (accounts.isEmpty() || accounts.isEmpty() || !accounts.get(0).getPassword().equals(pw)) {
                return "loginfailed";
            }
            Account account = accounts.get(0);
            model.addAttribute("account", account);
//  todo archive/queue account in case payment/refund is due, then delete  accountRepository.deleteByEmail(email);
            return "accountdeletedsuccessfully";
        } catch (Exception e) {
            return "loginfailed";
        }
    }
}
