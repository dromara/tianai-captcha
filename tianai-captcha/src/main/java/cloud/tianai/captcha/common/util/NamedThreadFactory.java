package cloud.tianai.captcha.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory that allows for custom thread names.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger THREAD_INDEX = new AtomicInteger(0);

    private final String basename;
    private final boolean daemon;

    /**
     * Creates a new instance of the factory.
     *
     * @param basename Basename of a new tread created by this factory.
     */
    public NamedThreadFactory(final String basename) {
        this(basename, true);
    }

    /**
     * Creates a new instance of the factory.
     *
     * @param basename Basename of a new tread created by this factory.
     * @param daemon   If true, marks new thread as a daemon thread
     */
    public NamedThreadFactory(final String basename, final boolean daemon) {

        this.basename = basename;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(final Runnable runnable) {

        final Thread thread = new Thread(runnable, basename + "-" + THREAD_INDEX.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }
}
