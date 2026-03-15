package red.aviora.redmc.tab.models;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TabAnimation {

	private final List<String> frames;
	private final int interval;
	private final boolean animated;
	private final AtomicInteger tickCounter = new AtomicInteger(0);
	private final AtomicInteger frameIndex = new AtomicInteger(0);

	public TabAnimation(List<String> frames, int interval, boolean animated) {
		this.frames = List.copyOf(frames);
		this.interval = interval;
		this.animated = animated;
	}

	public String getCurrentFrame() {
		if (frames.isEmpty()) return "";
		return frames.get(frameIndex.get() % frames.size());
	}

	public void tick() {
		if (!animated || frames.size() <= 1) {
			return;
		}

		int counter = tickCounter.incrementAndGet();
		if (counter >= interval) {
			tickCounter.set(0);
			frameIndex.updateAndGet(i -> (i + 1) % frames.size());
		}
	}

	public int getInterval() { return interval; }
	public boolean isAnimated() { return animated; }
	public List<String> getFrames() { return frames; }
}
