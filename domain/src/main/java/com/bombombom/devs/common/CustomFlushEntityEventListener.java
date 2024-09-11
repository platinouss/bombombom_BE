package com.bombombom.devs.common;

import java.util.Arrays;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.spi.FlushEntityEvent;
import org.springframework.stereotype.Component;


@Component
public class CustomFlushEntityEventListener extends DefaultFlushEntityEventListener {

    private static final Set<String> IGNORED_PROPERTIES = Set.of("updatedAt", "updatedBy");

    @Override
    protected void dirtyCheck(FlushEntityEvent event) throws HibernateException {
        super.dirtyCheck(event);

        String[] propertyNames = event.getEntityEntry().getPersister().getPropertyNames();
        int[] dirtyPropertyIndexes = event.getDirtyProperties();

        if (dirtyPropertyIndexes == null) {
            return;
        }

        if (Arrays.stream(dirtyPropertyIndexes).allMatch(
            index -> IGNORED_PROPERTIES.contains(propertyNames[index])
        )) {
            event.setDirtyProperties(null);
        }

    }

}
