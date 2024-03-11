package podsofkon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.TextMessage;
import javax.sql.DataSource;

import static podsofkon.XRApplication.jmsTemplate;

@RestController
public class XRDBController {

    private static Logger log = LoggerFactory.getLogger(XRDBController.class);

    @Autowired
    DataSource datasource;

    @GetMapping("/dbtest")
    public String dbtest() {
        log.debug("dbtest successful:" + datasource);
        return "dbtest successful datasource:"+datasource;
    }

    @GetMapping("/createSchema")
    public String createschema(@RequestParam("userName") String userName,
                               @RequestParam("password") String password) throws Exception {
        log.warn("createschema serviceName = " + userName +" creating database schema...");
        String returnString = new DBOperations().createSchema(userName, password);
//        try {
//            log.debug("created database schema, now creating secret for appName:" + appName +
//                    ", serviceName = " + serviceName  + " springBindingPrefix:" + springBindingPrefix);
//            returnString += " and " + new ApplySecret().createDataSourceSecret(
//                    appName, serviceName, servicePassword);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Exception occurred during create secret operation:" + e;
//        }
//        this.springBindingPrefix = springBindingPrefix; //todo base64
        return returnString;
    }
    @GetMapping("/createQueue")
    public String createQueue(@RequestParam("queueOwner") String queueOwner,
                               @RequestParam("queueName") String queueName) throws Exception {
        log.debug("createQueue queueOwner = " + queueOwner +
                ", queueName = " + queueName + " creating queue...");
        String returnString = new DBOperations().createQueue(queueOwner, queueName);
        return returnString;
    }
    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam("destination") String destination, @RequestParam("message") String message) throws Exception {
        log.debug("sendMessage destination = " + destination + " message = " + message +" ...");
        jmsTemplate.convertAndSend(destination, message);
        return "sendMessage message = " + message +" successful";
    }
    @GetMapping("/receiveMessage")
    public String receiveMessage(@RequestParam("destination") String destination) throws Exception {
        log.debug("receiveMessage destination = " + destination +" ...");
        return ((TextMessage)jmsTemplate.receive(destination)).getText();
    }
    @GetMapping("/createTable")
    public String createTable(@RequestParam("username") String username
            , @RequestParam("password") String password
            , @RequestParam("tableName") String tableName) throws Exception {
        log.warn("createTable tableName = " + tableName +" ..."+
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("createTable tableName = " + tableName +" ..."+
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("createTable tableName = " + tableName +" ..."+
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        new DBOperations().createTable(username, password, tableName);
        return "createTable tableName = " + tableName +" successful";
    }
    @GetMapping("/selectFromTable")
    public String selectFromTable(@RequestParam("username") String username
            , @RequestParam("password") String password
            ,  @RequestParam("tableName") String tableName) throws Exception {
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        log.warn("selectFromTable tableName = " + tableName +" ..." +
                "username = " + username + ", password = " + password + ", tableName = " + tableName);
        return new DBOperations().selectFromTable(username, password, tableName);
    }

}
