package gille.patricia.birthdayreminder

import androidx.room.Embedded
import androidx.room.Entity

data class Person(
    val name: String,
    val surname: String?,
) {
    fun isEqual(other: Person): Boolean {
        return name == other.name && surname == other.surname
    }
}

@Entity(primaryKeys = ["day", "month", "year", "name"])
data class Birthday(
    val day: Int,
    val month: Int,
    val year: Int,
    @Embedded val person: Person
) {
    fun isEqual(other: Birthday): Boolean {
        return person.isEqual(other.person) &&
                day == other.day && month == other.month &&
                year == other.year
    }
}
