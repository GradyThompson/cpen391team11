package com.example.smarticompanionapp;

import android.view.View;
import android.widget.ListView;

import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

//simple deletion test, most testing has been done by hand to ensure
//proper functionality using encoded examples, but this performs an example of
//instrumenting an element
public class DeleteTest {
    @Rule
    public ActivityTestRule<RecordingsActivity> activityRule =
            new ActivityTestRule<>(RecordingsActivity.class);

    @Test
    public void deleteTest() throws InterruptedException {
        onData(anything()).atPosition(0).onChildView(withId(R.id.optionsIcon)).perform(click());
        onView(withText("DELETE")).perform(click());
    }

}
