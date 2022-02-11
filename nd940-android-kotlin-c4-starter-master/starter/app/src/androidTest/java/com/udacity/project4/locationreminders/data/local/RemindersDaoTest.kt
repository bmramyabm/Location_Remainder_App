package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.Assert
import junit.framework.Assert.assertNull

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).build()
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @After
    fun closeDb() = database.close()


    @Test
    fun check_get_reminders() = runBlockingTest {
        // GIVEN
        val reminder = ReminderDTO("title 1", "description 1","location 1",
            (-360..360).random().toDouble(),(-360..360).random().toDouble())
        database.reminderDao().saveReminder(reminder)

        // WHEN - obtaining reminders from db
        val reminders = database.reminderDao().getReminders()

        // THEN - Only see 1 w/ correct values
        assertThat(reminders.size, `is`(1))
        assertThat(reminders[0].id, `is`(reminder.id))
        assertThat(reminders[0].title, `is`(reminder.title))
        assertThat(reminders[0].description, `is`(reminder.description))
        assertThat(reminders[0].location, `is`(reminder.location))
        assertThat(reminders[0].latitude, `is`(reminder.latitude))
        assertThat(reminders[0].longitude, `is`(reminder.longitude))
    }

    @Test
    fun check_deleting_reminders() = runBlockingTest {
        // Given - reminders inserted
        val remindersList = listOf<ReminderDTO>(ReminderDTO("title1", "description1",
            "location1",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
            ReminderDTO("title2", "description2",
                "location2",(-360..360).random().toDouble(),(-360..360).random().toDouble()))
        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        // WHEN - delete all reminders
        database.reminderDao().deleteAllReminders()

        // THEN - Nothing left
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }

    @Test
    fun check_reminder_does_not_exist() = runBlockingTest {
        // GIVEN
        val reminderId = UUID.randomUUID().toString()
        // WHEN
        val loaded = database.reminderDao().getReminderById(reminderId)
        // THEN - The loaded data should be  null.
        assertNull(loaded)
    }

}