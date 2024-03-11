package podsofkon.cli;

import java.util.Objects;

public class Microservice {

    String appName;
    String serviceName;

    public Microservice(String appName, String serviceName) {
        this.appName = appName;
        this.serviceName = serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Microservice that = (Microservice) o;
        return appName.equals(that.appName) && serviceName.equals(that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, serviceName);
    }
}
