package podsofkon.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USAGE")
@Data
@NoArgsConstructor
public class Usage implements Serializable {


    @EmbeddedId
    private UsageId usageId;

    @Column(name = "USAGE_COUNT")
    private int usageCount;

    //this is just for display convenience
    @Column(name = "USAGE_COST")
    private String usageCost;

    public Usage(UsageId usageId, int usageCount) {
        this.usageId = usageId;
        this.usageCount = usageCount;
    }

    public int incrementUsageCount() {
        return usageCount++;
    }
}
