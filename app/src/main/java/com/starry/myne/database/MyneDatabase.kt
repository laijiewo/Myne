/**
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starry.myne.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.starry.myne.database.library.LibraryDao
import com.starry.myne.database.library.LibraryItem
import com.starry.myne.database.progress.ProgressDao
import com.starry.myne.database.progress.ProgressData
import com.starry.myne.database.vocabulary.Vocabulary
import com.starry.myne.database.vocabulary.VocabularyDao
import com.starry.myne.helpers.Constants

@Database(
    entities = [LibraryItem::class, ProgressData::class, Vocabulary::class],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6)
    ]
)
abstract class MyneDatabase : RoomDatabase() {

    abstract fun getLibraryDao(): LibraryDao
    abstract fun getReaderDao(): ProgressDao
    abstract fun getVocabularyDao(): VocabularyDao

    companion object {

        private val migration3to4 = Migration(3, 4) { database ->
            database.execSQL("ALTER TABLE reader_table RENAME COLUMN book_id TO library_item_id")
        }
        private val migration5to6 = Migration(5, 6) { database ->
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS vocabulary (
                vocabulary_id INTEGER PRIMARY KEY AUTOINCREMENT,
                source_book_id INTEGER NOT NULL,
                vocabulary TEXT NOT NULL,
                source_language TEXT NOT NULL,
                target_language TEXT NOT NULL,
                translation TEXT NOT NULL
            )
        """)
        }

        @Volatile
        private var INSTANCE: MyneDatabase? = null

        fun getInstance(context: Context): MyneDatabase {
            /*
            if the INSTANCE is not null, then return it,
            if it is, then create the database and save
            in instance variable then return it.
            */
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyneDatabase::class.java,
                    Constants.DATABASE_NAME
                ).addMigrations(migration3to4, migration5to6).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}