package podsofkon.messaging;

import oracle.jms.AQjmsSession;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;

public class SpringLRAHotelParticipant {

    //explicit selector = opcode, and replyto/sendto required in POC should be unnecessary in final impl

//    @LRA(value = LRA.Type.MANDATORY)
//    @JmsListener(destination = "hotelreservationqueue", containerFactory = "lraqueueConnectionFactory")
    public String reserviceHotel(String message, AQjmsSession session) throws Exception {
        //reduce inventory
        return "jsonresponse";
    }

//    @Complete //selector = opcode
//    @JmsListener(destination = "hotelreservationqueue", containerFactory = "lraqueueConnectionFactory")
    public String complete(String message, AQjmsSession session) throws Exception {
        //cleanup
        return "jsonresponse";
    }

//    @Compensate //selector = opcode
//    @JmsListener(destination = "hotelreservationqueue", containerFactory = "lraqueueConnectionFactory")
    public String compensate(String message, AQjmsSession session) throws Exception {
        //restore inventory
        return "jsonresponse";
    }

}
