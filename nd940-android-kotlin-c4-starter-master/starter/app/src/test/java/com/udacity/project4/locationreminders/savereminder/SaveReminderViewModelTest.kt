package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.generator.qdox.model.annotation.AnnotationRemainder
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel : SaveReminderViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule

    var mainCoroutineRule = MainCoroutineRule()

    private val list = listOf(
        ReminderDataItem("title",
        "description",
        "location",(-360..360).random().toDouble(),
        (-360..360).random().toDouble())
    )
    private val firstItem = list[0]

    @After
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun check_no_title(){
        fakeDataSource = FakeDataSource((null))
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),
        fakeDataSource)
        firstItem.title = null
        saveReminderViewModel.validateAndSaveReminder(firstItem)
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
           CoreMatchers.`is`(R.string.err_enter_title))
    }

    @Test
    fun check_no_location(){
        fakeDataSource = FakeDataSource(null)
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
        firstItem.title = "Title1"
        firstItem.location = null
        saveReminderViewModel.validateAndSaveReminder(firstItem)
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_select_location))

    }
    @Test
    fun check_normal_loading() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(firstItem)
        Assert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true))
    }

}