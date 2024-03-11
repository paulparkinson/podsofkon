package podsofkon.messaging;

import oracle.jms.AQjmsSession;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
public class SpringLRAInitiatorTravelAgency {

//    @Autowired
//    JmsTemplate jmsTemplate; //configured to propagate LRA Id
//
//    @LRA(value = LRA.Type.REQUIRED, end = true)
//    @JmsListener(destination = "travelbookingqueue", containerFactory = "queueConnectionFactory")
//    public String bookTravel(String message, AQjmsSession session) throws Exception {
////        jmsTemplate.convertAndSend(hotelTopic(), "reservationrequestJSON");
////        jmsTemplate.convertAndSend(airlineTopic(), "reservationrequestJSON");
//        // block for replies and respond accordingly (ie exception for compensate, reply for complete)
//        return "bookingsuccessorfail";
//    }
//
//    @LRA(value = LRA.Type.REQUIRED, end = true)
//    @GetMapping("/bookTravel")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String bookTravel(String message, AQjmsSession session) throws Exception {
////        jmsTemplate.convertAndSend(session.getTopic(queueOwner, inventoryQueueName), jsonString);
////        jmsTemplate.convertAndSend(session.getTopic(queueOwner, inventoryQueueName), jsonString);
//        // block for replies and respond accordingly (ie exception for compensate, reply for complete)
//        return "bookingsuccessorfail";
//    }

}


