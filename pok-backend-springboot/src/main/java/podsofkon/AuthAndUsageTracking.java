package podsofkon;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import podsofkon.model.Account;
import podsofkon.model.Usage;
import podsofkon.model.UsageId;
import podsofkon.repository.AccountRepository;

import org.slf4j.Logger;
import podsofkon.repository.UsageRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AuthAndUsageTracking {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static AuthAndUsageTracking singleton;
    final AccountRepository accountRepository;
    final UsageRepository usageRepository;

//    Queue<Usage> usageQueue = new LinkedList(); //ArrayDeque may be faster
    Queue<UsageId> usageQueue = new ArrayDeque();

    public AuthAndUsageTracking(AccountRepository accountRepository, UsageRepository usageRepository) {
        this.accountRepository = accountRepository;
        this.usageRepository = usageRepository;
        singleton = this;
    }

    public static final String AILANGUAGE_SENTIMENTDETECTION = "AILANGUAGE_SENTIMENTDETECTION";
    public static final String AIVISION_IMAGEDETECTION = "AIVISION_IMAGEDETECTION";
    public static final String AIVISION_TEXTDETECTION = "AIVISION_TEXTDETECTION";
    public static final String OPENAI_IMAGEFORTEXT = "OPENAI_IMAGEFORTEXT";
    public static final String OPENAI_CHAT = "OPENAI_CHAT";
    public static final String SPEECHAI_TRANSCRIBE = "SPEECHAI_TRANSCRIBE";
    public static final String SPEECHAI_TRANSLATE = "SPEECHAI_TRANSLATE";

    public static AuthAndUsageTracking instance() {
        return singleton;
    }



    public static double costPerUseForServiceUsageName(String usageName) {
        switch (usageName) {
            case AuthAndUsageTracking.AIVISION_IMAGEDETECTION:
                return .02;
            case AuthAndUsageTracking.OPENAI_IMAGEFORTEXT:
                return .04;
            case AuthAndUsageTracking.OPENAI_CHAT:
                return .03;
            case AuthAndUsageTracking.AILANGUAGE_SENTIMENTDETECTION:
                return .04;
            case AuthAndUsageTracking.AIVISION_TEXTDETECTION:
                return .04;
            case AuthAndUsageTracking.SPEECHAI_TRANSCRIBE:
                return .04;
            case AuthAndUsageTracking.SPEECHAI_TRANSLATE:
                return .04;
            default:
                return -1;

        }
    }
    public String auth(HttpServletRequest request, String methodName, String token) throws Exception {
        if(token != null) return processToken(token);
        String authorizationString;
        Enumeration<String> authorization = request.getHeaders("Authorization");
        if (!authorization.hasMoreElements()) {
            log.info(methodName + "no authorization found");
            throw new AccessDeniedException("no authorization found");
        }
        authorizationString = authorization.nextElement();
        if(authorizationString.startsWith("Bearer ")) authorizationString = authorizationString.substring(7);
        log.info(methodName + "Authorization = [" + authorizationString + "]");
        Account account = accountRepository.findByAuthKey(authorizationString);
        log.info("accountRepository.findByAuthKey account = " + account);
        if (account == null) {
            log.info(methodName + " user not found");
            throw new AccessDeniedException(methodName + " user not found");
        }
        String emailForAuthEky = account.getEmail();
        log.info(methodName + " emailForAuthEky = " + emailForAuthEky);
        return emailForAuthEky;
    }

    public String processToken(String authorizationString) throws Exception {
        log.info( "site Authorization = " + authorizationString);
        Account account = accountRepository.findByAuthKey(authorizationString);
        log.info("accountRepository.findByAuthKey account = " + account);
        if(account == null) {
            log.info("site authorization token not valid");
            throw new Exception("site authorization token not valid");
        }
        String emailForAuthEky = account.getEmail();
        log.info("site auth emailForAuthEky = " + emailForAuthEky);
        return emailForAuthEky;
    }


    @PostConstruct
    public void init() throws Exception {
        new Thread(new UsageTrackerBatchUpdate()).start();
    }


    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    public void addUsage(String emailForAuthEky, String usageName) {
//        log.info("usage added for emailForAuthEky:" + emailForAuthEky + " usageName:" + );
        usageQueue.add(new UsageId(emailForAuthEky, formatter.format(new Date()), usageName));
    }

    class UsageTrackerBatchUpdate implements Runnable {

        private final Logger log = LoggerFactory.getLogger(this.getClass());
        @Override
        public void run() {
            while (true) {
                try {
                    UsageId usageId = usageQueue.poll();
                    if(usageId == null) {
                        Thread.sleep(1000 * 60);
//                        log.info("usage id null");
                    } else {
                        Usage usage;
                        Optional<Usage> usageOptional = usageRepository.findById(usageId);
                        log.info("usageOptional.isPresent():" + usageOptional.isPresent());
                        if (usageOptional.isPresent()) {
                            usage = usageOptional.get();
                        } else {
                            usage = new Usage();
                            usage.setUsageId(usageId);
                        }
                        usage.incrementUsageCount();
                        usageRepository.save(usage);
//                        log.info("successfully incremented usage:" + usage);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
