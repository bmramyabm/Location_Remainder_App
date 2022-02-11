package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (var reminders:MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false

    fun setshouldReturnError(value : Boolean){
        this.shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("No reminders to return", 404)
        }

        reminders?.let { return Result.Success(it) }
        return Result.Error("No reminders to return")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error ("Reminder error")
        }
        val reminder = reminders?.find { it.id == id }
        return if (reminder == null) Result.Error("Could not find reminder")
        else Result.Success(reminder)
    }


    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}
