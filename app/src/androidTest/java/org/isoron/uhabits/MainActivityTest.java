package org.isoron.uhabits;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.isoron.uhabits.models.Habit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.isoron.uhabits.HabitMatchers.withName;
import static org.isoron.uhabits.HabitViewActions.toggleAllCheckmarks;
import static org.isoron.uhabits.MainActivityActions.addHabit;
import static org.isoron.uhabits.MainActivityActions.assertHabitExists;
import static org.isoron.uhabits.MainActivityActions.assertHabitsDontExist;
import static org.isoron.uhabits.MainActivityActions.assertHabitsExist;
import static org.isoron.uhabits.MainActivityActions.deleteHabit;
import static org.isoron.uhabits.MainActivityActions.deleteHabits;
import static org.isoron.uhabits.MainActivityActions.selectHabits;
import static org.isoron.uhabits.MainActivityActions.typeHabitData;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest
{
    @Rule
    public IntentsTestRule<MainActivity> activityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void skipTutorial()
    {
        try
        {
            for (int i = 0; i < 10; i++)
                onView(allOf(withClassName(endsWith("AppCompatImageButton")),
                        isDisplayed())).perform(click());
        }
        catch (NoMatchingViewException e)
        {
            // ignored
        }
    }

    @Test
    public void testArchiveHabits()
    {
        List<String> names = new LinkedList<>();
        Context context = InstrumentationRegistry.getTargetContext();

        for(int i = 0; i < 3; i++)
            names.add(addHabit());

        selectHabits(names);
        onView(withContentDescription(R.string.archive))
                .perform(click());
        assertHabitsDontExist(names);

        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.string.show_archived))
                .perform(click());

        assertHabitsExist(names);
        selectHabits(names);
        onView(withContentDescription(R.string.unarchive))
                .perform(click());

        openActionBarOverflowOrOptionsMenu(context);
        onView(withText(R.string.show_archived))
                .perform(click());

        assertHabitsExist(names);
        deleteHabits(names);
    }

    @Test
    public void testAddInvalidHabit()
    {
        onView(withId(R.id.action_add))
                .perform(click());

        typeHabitData("", "", "15", "7");

        onView(withId(R.id.buttonSave)).perform(click());
        onView(withId(R.id.input_name)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddHabitAndToggleCheckmarks()
    {
        String name = addHabit(true);

        onData(allOf(is(instanceOf(Habit.class)), withName(name)))
                .onChildView(withId(R.id.llButtons))
                .perform(toggleAllCheckmarks());

        onData(allOf(is(instanceOf(Habit.class)), withName(name)))
                .onChildView(withId(R.id.label))
                .perform(click());

        onView(withId(R.id.punchcardView))
                .perform(scrollTo());
    }

    @Test
    public void testEditHabit()
    {
        String name = addHabit();

        onData(allOf(is(instanceOf(Habit.class)), withName(name)))
                .onChildView(withId(R.id.label))
                .perform(longClick());

        onView(withContentDescription(R.string.edit))
                .perform(click());

        String modifiedName = "Modified " + new Random().nextInt(10000);
        typeHabitData(modifiedName, "", "1", "1");

        onView(withId(R.id.buttonSave))
                .perform(click());

        assertHabitExists(modifiedName);
        deleteHabit(modifiedName);
    }
}
