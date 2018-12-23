package wang.xiaoluobo.akka;

import java.io.Serializable;

public class Greeting implements Serializable {

    public final String message;

    public Greeting(String message) {
        this.message = message;
    }
}
