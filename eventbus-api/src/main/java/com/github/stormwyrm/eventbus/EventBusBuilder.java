package com.github.stormwyrm.eventbus;

class EventBusBuilder {
    boolean isSkipGenerateIndex;

    EventBusBuilder() {
    }

    public EventBusBuilder skipGenerateIndex(boolean skipGenerateIndex) {
        isSkipGenerateIndex = skipGenerateIndex;
        return this;
    }

    public boolean isSkipGenerateIndex() {
        return isSkipGenerateIndex;
    }

    public EventBus build() {
        return new EventBus(this);
    }
}
