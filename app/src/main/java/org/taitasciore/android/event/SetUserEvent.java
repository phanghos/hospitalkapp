package org.taitasciore.android.event;

import org.taitasciore.android.model.User;

/**
 * Created by roberto on 03/05/17.
 */

public class SetUserEvent {

    public User user;

    public SetUserEvent(User user) {
        this.user = user;
    }
}
