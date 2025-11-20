package com.cs407.sharedspace.data

import com.cs407.sharedspace.R

enum class ChoreRepeats(val repeatName: String) {
    DAILY("daily"),
    TWICE_WEEKLY("twice a week"),
    WEEKLY("weekly"),
    EVERY_TWO_WEEKS("every two weeks"),
    MONTHLY("monthly")
}

// Will be used for different default Icon Options
enum class ChoreImage(
    val id: Int,
    val contentDescription: String) {
    //TODO: Add Images to enum class
    CLEANING(R.drawable.ic_chore, "Cleaning")
}

data class Chore(
    val choreId: Int,
    val choreName: String,
    val choreAssigneeId: Int,
    val choreAssignee: String,
    val choreRepeats: ChoreRepeats,
    val choreImage: ChoreImage,
    val choreTicked: Boolean
)

/*
@Entity(
    indices = [Index(
        value = ["userUID"], unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0, val userUID: String = ""
)*/