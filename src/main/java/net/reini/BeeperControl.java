package net.reini;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class BeeperControl {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		try {
			new BeeperControl().beepForAnHour();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void beepForAnHour() throws Exception {
		final Runnable beeper = () -> System.out.println("beep");

		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);

		scheduler.schedule(() -> {
			beeperHandle.cancel(true);
		}, 1, HOURS);

		TimeUnit.SECONDS.sleep(30);

		System.out.println("cancel beeper " + beeperHandle.cancel(true));
		System.out.println("beeper canceled " + beeperHandle.isCancelled());
		System.out.println("beeper done " + beeperHandle.isDone());
		TimeUnit.SECONDS.sleep(10);

	}
}