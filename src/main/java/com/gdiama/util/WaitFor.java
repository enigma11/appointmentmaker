package com.gdiama.util;

import com.google.code.tempusfugit.temporal.Condition;

import java.util.List;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

public class WaitFor {
    public static <T> T waitForOptionToDisplayAndReturn(final ElementFinder<?> elementFinder) throws Exception {
        final Object[] category = new Object[1];
        waitOrTimeout(new Condition() {
            public boolean isSatisfied() {
                try {
                    category[0] = elementFinder.find();
                    if (category[0] instanceof List) {
                        return ((List) category[0]).size() > 0;
                    }
                    if (category[0] != null) {
                        return true;
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }, timeout(seconds(120)));
        return (T) category[0];
    }
}
