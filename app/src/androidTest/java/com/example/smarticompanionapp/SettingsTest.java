package com.example.smarticompanionapp;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SettingsTest {
    @Rule
    public ActivityTestRule<SettingsActivity> activityRule =
            new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void settingsTest1() throws InterruptedException {
        onView(withId(R.id.defaultButton)).perform(click());

        onView(withText("Maximum recording length")).perform(click());
        onView(withText("4 minutes")).perform(click());
        onView(withText("4 minutes")).check(matches(isDisplayed()));
    }

    @Test
    public void settingsTest2() throws InterruptedException {
        onView(withText("Minimum recording length")).perform(click());
        onView(withText("1 minute")).perform(click());
        onView(withText("1 minute")).check(matches(isDisplayed()));
    }

    @Test
    public void settingsTest3() throws InterruptedException {
        onView(withText("Retain recordings for:")).perform(click());
        onView(withText("Until manual deletion")).perform(click());
        onView(withText("Until manual deletion")).check(matches(isDisplayed()));
    }

    //not going to check video bitrate for now since it has placeholder values

    @Test
    public void settingsTest4() throws InterruptedException {
        onView(withText("Enable push notifications")).perform(click());
        onView(withText("Enable physical notifications")).perform(click());
        onView(withText("Severity threshold for notifications")).perform(click());
        onView(withText("10/10")).perform(click());
        onView(withText("10/10")).check(matches(isDisplayed()));
    }

    @Test
    public void settingsTest5() throws InterruptedException {
        onView(withId(R.id.defaultButton)).perform(click());

        onView(withText("2 minutes")).check(matches(isDisplayed()));
        onView(withText("10 seconds")).check(matches(isDisplayed()));
        onView(withText("2 weeks")).check(matches(isDisplayed()));
        onView(withText("3/10")).check(matches(isDisplayed()));
    }

    //Should have a test for the send button, when it works
}
