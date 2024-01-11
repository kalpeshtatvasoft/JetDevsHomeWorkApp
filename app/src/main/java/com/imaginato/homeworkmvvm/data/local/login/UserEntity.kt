package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    var userId: String,

    @ColumnInfo(name = "username")
    var userName: String,

    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean,

    @ColumnInfo(name = "x_Acc")
    var xAcc: String,
)