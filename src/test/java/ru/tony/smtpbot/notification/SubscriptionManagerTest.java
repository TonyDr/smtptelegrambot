package ru.tony.smtpbot.notification;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SubscriptionManagerTest {


    private SubscriptionManager sut;

    @Before
    public void beforeMethod() {
        sut = new SubscriptionManager();
    }

    @Test
    public void shouldCorrectlySubscribeToEmail() {
        sut.subscribe(123L, "test");
        sut.subscribe(123L, "test2");
        sut.subscribe(124L, "test");


        assertEquals(Set.of("test", "test2"), sut.findSubscriptions(123L));
        assertEquals(Set.of(123L, 124L), sut.findSubscribers("test"));
    }

    @Test
    public void shouldCorrectlyUnsubscribeFromEmail() {
        sut.subscribe(123L, "test");
        sut.subscribe(123L, "test2");
        sut.subscribe(124L, "test");

        sut.unsubscribe(123L, "test");

        assertEquals(Set.of("test2"), sut.findSubscriptions(123L));
        assertEquals(Set.of(124L), sut.findSubscribers("test"));
    }
}