package org.joverseer.ui;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * A copy of SimpleApplicationEventCaster to act as a basis for smarter event casting.
 * Now tolerates exceptions being thrown by the event handlers.
 * @author Dave
 *
 */
public class SmartApplicationEventCaster extends AbstractApplicationEventMulticaster {
	Log log = LogFactory.getLog(SmartApplicationEventCaster.class);
	private TaskExecutor taskExecutor = new SyncTaskExecutor();


	/**
	 * Set the TaskExecutor to execute application listeners with.
	 * <p>Default is a SyncTaskExecutor, executing the listeners synchronously
	 * in the calling thread.
	 * <p>Consider specifying an asynchronous TaskExecutor here to not block the
	 * caller until all listeners have been executed. However, note that asynchronous
	 * execution will not participate in the caller's thread context (class loader,
	 * transaction association) unless the TaskExecutor explicitly supports this.
	 * @see org.springframework.core.task.SyncTaskExecutor
	 * @see org.springframework.core.task.SimpleAsyncTaskExecutor
	 * @see org.springframework.scheduling.timer.TimerTaskExecutor
	 */
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = (taskExecutor != null ? taskExecutor : new SyncTaskExecutor());
	}

	/**
	 * Return the current TaskExecutor for this multicaster.
	 */
	protected TaskExecutor getTaskExecutor() {
		return this.taskExecutor;
	}


	@Override
	public void multicastEvent(final ApplicationEvent event) {
		final JOverseerEvent  joe;
		if (event instanceof JOverseerEvent) {
			joe = (JOverseerEvent)event;
		} else {
			joe = null;
		}
		for (Iterator it = getApplicationListeners().iterator(); it.hasNext();) {
			final ApplicationListener listener = (ApplicationListener) it.next();
			getTaskExecutor().execute(new Runnable() {
				@Override
				public void run() {
					if (SmartApplicationEventCaster.this.log.isInfoEnabled()) {
						if (joe != null) {
							SmartApplicationEventCaster.this.log.info(listener.getClass().getSimpleName() + " receiving " + joe.getEventType());
						} else {
							SmartApplicationEventCaster.this.log.info(listener.getClass().getSimpleName() + " receiving " + event.getClass().getSimpleName());
						}
					}
					try {
						listener.onApplicationEvent(event);
					} catch (Exception exc) {
						if (joe != null) {
							SmartApplicationEventCaster.this.log.error(listener.getClass().getSimpleName() + " gave exception receiving " + joe.getEventType());
							SmartApplicationEventCaster.this.log.error(exc.getClass().getSimpleName());
							if (exc.getMessage() != null) {
								SmartApplicationEventCaster.this.log.error(exc.getMessage());
							}
							if (exc.getStackTrace() != null) {
								SmartApplicationEventCaster.this.log.error(exc.getStackTrace());
							}
						}
					}
				}
			});
		}
	}
}
