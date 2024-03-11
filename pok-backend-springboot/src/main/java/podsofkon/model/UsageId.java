package podsofkon.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UsageId implements Serializable {
    public String email;
    public String usageDate;
    public String usageName;


    public UsageId() {

    }

//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }

    @Override
    public boolean equals(Object obj) {
        UsageId that = (UsageId) obj;
        return this.email.equals(that.email) && this.usageDate.equals((that.usageDate)) && this.usageName.equals((that.usageDate));
    }

    public UsageId(String email, String date, String usageName) {
        this.email = email;
        this.usageDate = date;
        this.usageName = usageName;
    }
}
