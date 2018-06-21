package io.github.u2ware.apps.login;

import javax.persistence.Entity;

import io.github.u2ware.apps.AbstractUserEventEntity;

@Entity
public class UserEvent extends AbstractUserEventEntity {

	public UserEvent() {
	}

	public UserEvent(String username, String eventType) {
		this.setUsername(username);
		this.setEventType(eventType);
		this.setEventTimestamp(System.currentTimeMillis());
	}
}