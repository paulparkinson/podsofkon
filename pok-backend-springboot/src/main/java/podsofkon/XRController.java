package podsofkon;


import podsofkon.cli.Microservice;
import podsofkon.cli.MicroserviceDetails;
import podsofkon.container.ContainerOperations;
import podsofkon.k8s.*;
import podsofkon.messaging.OracleAQConfiguration;
import podsofkon.oci.CreateContainerRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import podsofkon.upload.storage.StorageService;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;

@RestController
@RequestMapping("/xr")
public class XRController {

    private static Logger log = LoggerFactory.getLogger(XRController.class);
    public static String registryUrl = System.getenv("registry.url");
    private final StorageService storageService;

    @Autowired
    public XRController(StorageService storageService) {
        this.storageService = storageService;
    }

    public static Map<Microservice, MicroserviceDetails> microserviceDetailsMap = new HashMap<>();
    public static String springBindingPrefix;
    public static String schemaName;
    public static final String VERSION = "121522";

    @GetMapping("/connect")
    public String connect() {
        log.debug("connect successful");
        return "connect successful server version:" + VERSION;
    }

    @GetMapping("/version")
    public String version() {
        log.debug("version:" + VERSION);
        return "server version:" + VERSION;
    }


    @PostMapping("/testpost")
    public String testpost() {
        log.debug("testpost successful");
        return "testpost successful";
    }

        @Autowired
        DataSource datasource;

    private static Connection conn;
    private static PreparedStatement preparedStatementIncrementScore;
    private static PreparedStatement preparedStatementClearScore;
    @GetMapping("/createTables")
    public String incrementScore() throws Exception {
        log.debug("createTables for datasource:" + datasource + "...");
        initConn();
        conn.createStatement().execute("create table currentgame( playername varchar(256), score number(10)  )");
        conn.createStatement().execute("insert into currentgame values ( 'player1', 0  )");
        conn.createStatement().execute("insert into currentgame values ( 'player2', 0  )");
        conn.createStatement().execute("create table scores( playername varchar(256), score number(10) )");
        String returnString = "createTables success";
        return returnString;
    }
    @GetMapping("/movescores")
    public String movescores(@RequestParam("player1Name") String player1Name,
                                             @RequestParam("player2Name") String player2Name) throws Exception {
        System.out.println("movescores for datasource:" + datasource + "...");
        initConn();
        conn.createStatement().execute(
                "insert into scores( playername , score ) select '" + player1Name +"', score from currentgame " +
                        "where playername='player1'");
        conn.createStatement().execute(
                "insert into scores( playername , score ) select '" + player2Name +"', score from currentgame " +
                        "where playername='player2'");
        System.out.println("movescores for datasource:" + datasource + "...");
        //clear the current game...
        clearScore("player1");
        clearScore("player2");
        return "movescores success";
    }

    @GetMapping("/incrementScore")
    public String incrementScore(@RequestParam("playerName") String playerName,
                                 @RequestParam("amount") int amount) throws Exception {
        log.debug("incrementScore for playerName:" + playerName + "...");
        log.debug("incrementScore for datasource:" + datasource + "...");
        updateScore(playerName, amount);
        return "incrementScore success";
    }

    private void updateScore(String playerName, int amount) throws SQLException {
        initConn();
        try {
            preparedStatementIncrementScore.setInt(1, amount);
            preparedStatementIncrementScore.setString(2, playerName);
            preparedStatementIncrementScore.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void clearScore(String playerName) throws SQLException {
        initConn();
        try {
            preparedStatementClearScore.setString(1, playerName);
            preparedStatementClearScore.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initConn() throws SQLException {
        if (conn == null || preparedStatementIncrementScore ==null) {
            conn = datasource.getConnection();
            preparedStatementIncrementScore = conn.prepareStatement(
                    "UPDATE currentgame SET score=score+? WHERE playername=?");
            preparedStatementClearScore = conn.prepareStatement(
                    "UPDATE currentgame SET score=0 WHERE playername=?");
        }
    }

    @GetMapping("/createApp")
    public String create(@RequestParam("appName") String appName) throws Exception {
        String returnString = "";
        try {
            log.debug("create application/namespace for appName:" + appName + "...");
            returnString +=  new ApplyNamespace().createNamespace(appName);
        } catch (Exception e) {
            return "Exception occurred during create operation (likely due to AlreadyExists if namespace is in the process of being deleted)" + e.getMessage();
        }
        try {
            log.debug("create pull auth secret for appName:" + appName + "...");
            returnString += " and " + new ApplySecret().createRegAuthSecret(appName);
        } catch (Exception e) {
            return "Exception occurred during create secret operation:" + e.getMessage();
        }
        return returnString;
    }

    @GetMapping("/deleteApp")
    public String delete(@RequestParam("appName") String appName) throws Exception {
        log.debug("delete application/namespace...");
        String returnString = "";
        try {
            returnString+= new DeleteNamespace().deleteNamespace(appName);
        } catch (Exception e) {
            return "Exception occurred during delete operation:" + e.getMessage();
        }
        //todo if deleting all schemas for an app/ns...
//        try {
//            returnString+= new DBOperations().deleteSchema(appName);
//        } catch (Exception e) {
//            return "Exception occurred during delete operation:" + e.getMessage();
//        }
        return returnString;
    }

    @GetMapping("/deleteDeployment")
    public String delete(@RequestParam("appName") String appName, String deploymentName) throws Exception {
        log.debug("delete application/namespace...");
        try {
            return new DeleteDeployment().deleteDeployment(appName, deploymentName);
        } catch (Exception e) {
            return "Exception occurred during delete operation:" + e.getMessage();
        }
    }
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    @PostMapping("/db")
    public String db(@RequestParam("key") String key, @RequestParam("value") String value)
            throws Exception {
        log.info("db key = " + key + ", value = " + value);
        if (!key.equalsIgnoreCase("deRez") && !key.equalsIgnoreCase("deRezConfig"))
            throw new Exception("invalid key");
        Connection connection = new OracleAQConfiguration().dataSource().getConnection();
//        Connection connection = new DBOperations().getPoolDataSource().getConnection();

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet res = meta.getTables(null, null, null, new String[] { "TABLE" });
        String returnString = "tables... ";
        while (res.next()) {
            String table_name = res.getString("TABLE_NAME");
            if (table_name.equalsIgnoreCase("properties"))
                returnString += table_name + " ";
            System.out.println(returnString);
        }
        return returnString;
    }
//
//    @PostMapping("/uploadfile")
//    public String upload(@RequestParam("file") MultipartFile file)
//    @PostMapping("/upload")
//    public String upload(@RequestParam("serviceName") String serviceName,
//                         @RequestParam("appName") String appName, @RequestParam("file") MultipartFile file)
//            throws Exception {
//                log.info("got a file, now analyze");
//                return new AnalyzeImageExample().main(file.getBytes());
//        String appName = "test", serviceName = "test";
//        log.debug("upload file:" + file.getOriginalFilename());
//        Path path = storageService.store(appName, serviceName, file);
//     //   File baseDir = storageService.getRootLocation().toFile();
//        //this provides parellel for different sservices but not for same service (which makes sense)
//        MicroserviceDetails microserviceDetails = microserviceDetailsMap.get(new Microservice(appName, serviceName));
//        if(microserviceDetails == null) {
//            microserviceDetailsMap.put(new Microservice(appName, serviceName),
//                    new MicroserviceDetails(path, file));
//        } else { //todo currently just does the same if exists...
//            microserviceDetailsMap.put(new Microservice(appName, serviceName),
//                    new MicroserviceDetails(path, file));
//        }
//        log.debug("upload successful for file:" + file);
//        return "upload successful";
//    }

//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//        Resource file = storageService.loadAsResource(filename);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }

    @GetMapping("/imageBuildAndPush")
    public String dockerBuildAndPush(@RequestParam("appName") String appName,
                                     @RequestParam("serviceName") String serviceName,
                                     @RequestParam("imageVersion") String imageVersion,
                                     @RequestParam("javaVersion") String javaVersion) throws Exception {
        log.debug("docker build and push appName:" + appName + " serviceName:" + serviceName +
                " imageVersion:" + imageVersion + " javaVersion:" + javaVersion);
        File baseDir = storageService.getRootLocation().toFile();
        new ContainerOperations().buildAndPush(baseDir, appName, serviceName, imageVersion, javaVersion);
        deleteDirectory(baseDir);
        return "docker build and push successful";
    }

    void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
//				deleteDirectory(file);
                file.delete();
            }
        }
//		return directoryToBeDeleted.delete();
    }

//    @GetMapping("/createDeploymentAndService")
//    public String createDeploymentAndService(@RequestParam("isRedeploy") String isRedeploy,
//                                             @RequestParam("serviceName") String serviceName,
//                                             @RequestParam("appName") String appName,
//                                             @RequestParam("imageVersion") String imageVersion) {
//        log.debug("create deployment and service  = " + serviceName + ", appName = " + appName +
//                ", isRedeploy = " + isRedeploy);
//        if (isRedeploy.equalsIgnoreCase("false")) {
//            try {
//                new ApplyService().createService(appName, serviceName);
//            } catch (Exception e) {
//                return "Exception occurred during create service operation:" + e.getMessage();
//            }
//        }
//        try {
//            new ApplyDeployment().createDeployment(isRedeploy, appName, serviceName, imageVersion);
//        } catch (Exception e) {
//            return "Exception occurred during deployment operation:" + e;
//        }
//        return "create deployment and service  = " + serviceName + ", appName = " + appName +
//                ", isRedeploy = " + isRedeploy + " successful";
//    }
    @GetMapping("/createDeployment")
    public String createDeployment(@RequestParam("appName") String appName, @RequestParam("serviceName") String serviceName) {
        log.debug("create deployment and appName  = " + appName );
        log.debug("create deployment and service  = " + serviceName );
        try {
            new ApplyDeployment().createDeployment(appName, serviceName);
        } catch (Exception e) {
            return "Exception occurred during create deployment operation:" + e;
        }
        try {
            incrementScore(appName, 200);
        } catch (Exception e) {
            return "Exception occurred during incrementScoreoperation:" + e;
        }
        return "create deployment and service  = " + serviceName + " successful";
    }

//    @GetMapping("/list")
//    public String list(@RequestParam("appName") String appName) throws Exception {
//        log.debug("list pods info...");
//        try {
//            return new ListInfoForNamespace().getPodsInfoForNamespace(appName);
//        } catch (Exception e) {
//            return "Exception occurred during list operation:" + e;
//        }
//    }

    @GetMapping("/updateOracleSpringAdminSecret")
    public String updateOracleSpringAdminSecret(
                               @RequestParam("password") String password) throws Exception {
        String returnString = "";
        log.debug("updateOracleSpringAdminSecret...");
        try {
            returnString += new ApplySecret().updateOracleSpringAdminSecret(password);
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred during updateOracleSpringAdminSecret operation:" + e;
        }
        log.debug("restartAdminServer...");
        //todo temp solution for admin change-password to take effect
        TimerTask exitApp = new TimerTask() {
            public void run() {
                System.exit(0);
            }
        };
        new Timer().schedule(exitApp, new Date(System.currentTimeMillis()+5*1000));
        return returnString;
    }
    @GetMapping("/createRepos")
    public String createRepos(
                               @RequestParam("compartmentid") String compartmentid,
                               @RequestParam("reposName") String reposName) throws Exception {
        log.debug("updateOracleSpringAdminSecret...");
        try {
            return new CreateContainerRepos().createReposForImage(compartmentid, reposName);
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred during updateOracleSpringAdminSecret operation:" + e;
        }
    }

}
