package wang.xiaoluobo.jdk8;

public class Streams {

    public enum MyStatus {
        OPEN, CLOSED
    }

    public static final class MyTask {
        private final MyStatus myStatus;
        private final Integer points;

        MyTask(final MyStatus status, final Integer points) {
            this.myStatus = status;
            this.points = points;
        }

        public Integer getPoints() {
            return points;
        }

        public MyStatus getMyStatus() {
            return myStatus;
        }

        @Override
        public String toString() {
            return String.format("[%s, %d]", myStatus, points);
        }
    }
}
