package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.ExpectFailure.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var reminderListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource
    // provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val reminderList = listOf(
        ReminderDTO("title1","description1","location1",0.0,0.0),
        ReminderDTO(
            "title 2",
            "description 2",
            "location 2",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title 3",
            "description 3",
            "location 3",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ))
    private val reminder1 = reminderList[0]
    private val reminder2 = reminderList[1]
    private val reminder3 = reminderList[2]

    @After
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun check_load_reminders_when_error() = runBlockingTest {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        fakeDataSource.setshouldReturnError(true)

        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`("No reminders to return"))

    }

    @Test
    fun check_loading(){
        fakeDataSource = FakeDataSource((mutableListOf()))
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )


    }
    @Test
    fun check_list_reminders() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue(),
            (CoreMatchers.not(emptyList()))
        )
        MatcherAssert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue().size,
            CoreMatchers.`is`(remindersList.size)
        )
    }

    @Test
    fun check_returnError() {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("No reminders to return")
        )
    }


}