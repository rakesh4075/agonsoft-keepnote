package com.keepnote.notesDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")

data class Notes(

    @PrimaryKey(autoGenerate = true) var noteId:Long = 0L,

    @ColumnInfo(name = "title") var title:String?,

    @ColumnInfo(name = "content") var content:String = "",

    @ColumnInfo(name = "notecolor") var notecolor:Int =0,

    @ColumnInfo(name = "isLocked") var islocked:Int =0,

    @ColumnInfo(name = "isDeleted") var isDeleted:Int =0,

    @ColumnInfo(name = "isFavourite") var isFavourite:Int =0,

    @ColumnInfo(name = "noteCreatedMills") var notecreatedMills:Long = System.currentTimeMillis(),

    @ColumnInfo(name = "noteUpdateMills") var noteupdateMills: Long = 0
)