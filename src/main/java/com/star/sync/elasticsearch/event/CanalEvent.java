package com.star.sync.elasticsearch.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:22:00
 */
public abstract class CanalEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CanalEvent(Entry source) {
        super(source);
    }

    public Entry getEntry() {
        return (Entry) source;
    }
}
