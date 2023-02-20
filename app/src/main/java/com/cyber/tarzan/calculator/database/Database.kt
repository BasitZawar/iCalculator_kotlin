package com.cyber.tarzan.calculator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cyber.tarzan.calculator.database.dao.HistoryDao
import com.cyber.tarzan.calculator.database.model.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

}