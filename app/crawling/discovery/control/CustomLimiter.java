package crawling.discovery.control;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

public class CustomLimiter {
    private RateLimiter guavaLimiter;
    private Throttle apiThrottle = new Throttle();

    public CustomLimiter() {
        this.guavaLimiter = RateLimiter.create(.8);
    }

    public double acquire() {
        try {
            apiThrottle.waitChoke();
        } catch (Exception e) {
            throw new IllegalStateException("Someone else handle this");
        }
        return guavaLimiter.acquire();
    }

    public void rateLimitNotify() {
        apiThrottle.startChoke();
    }

    class Throttle {
        private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        private ScheduledFuture future;

        public void startChoke() {
            future = executor.schedule(new EndThrottle(this), 5, TimeUnit.SECONDS);
        }

        public void waitChoke() throws InterruptedException, ExecutionException {
            if(future == null || future.isDone() || future.isCancelled()) 
                return;
            future.get();
        }
    }

    class EndThrottle implements Runnable {

        private Throttle throttle;
        @Override 
        public void run() {
            System.out.println("I feel like I should be doing something with the throttle here");
        }

        public EndThrottle(Throttle throttle) {
            this.throttle = throttle;
        }
    }
}