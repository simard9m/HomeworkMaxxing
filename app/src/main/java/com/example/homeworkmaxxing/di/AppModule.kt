package com.example.homeworkmaxxing.di

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.homeworkmaxxing.data.local.AppDatabase
import com.example.homeworkmaxxing.data.local.CoursDao
import com.example.homeworkmaxxing.data.local.RoutineDao
import com.example.homeworkmaxxing.data.local.SessionDao
import com.example.homeworkmaxxing.util.ValidationRules
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val validationCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            createValidationTriggers(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            createValidationTriggers(db)
        }

        private fun createValidationTriggers(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TRIGGER IF EXISTS validate_cours_nom_insert")
            db.execSQL("DROP TRIGGER IF EXISTS validate_cours_nom_update")
            db.execSQL("DROP TRIGGER IF EXISTS validate_routine_nom_insert")
            db.execSQL("DROP TRIGGER IF EXISTS validate_routine_nom_update")
            db.execSQL("DROP TRIGGER IF EXISTS validate_routine_description_insert")
            db.execSQL("DROP TRIGGER IF EXISTS validate_routine_description_update")

            db.execSQL(
                """
                CREATE TRIGGER validate_cours_nom_insert
                BEFORE INSERT ON cours
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'cours_nom_too_long')
                    WHERE LENGTH(NEW.nom) > ${ValidationRules.MAX_COURS_NOM_LENGTH};
                END
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TRIGGER validate_cours_nom_update
                BEFORE UPDATE ON cours
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'cours_nom_too_long')
                    WHERE LENGTH(NEW.nom) > ${ValidationRules.MAX_COURS_NOM_LENGTH};
                END
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TRIGGER validate_routine_nom_insert
                BEFORE INSERT ON routines
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'routine_nom_too_long')
                    WHERE LENGTH(NEW.nom) > ${ValidationRules.MAX_ROUTINE_NOM_LENGTH};
                END
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TRIGGER validate_routine_nom_update
                BEFORE UPDATE ON routines
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'routine_nom_too_long')
                    WHERE LENGTH(NEW.nom) > ${ValidationRules.MAX_ROUTINE_NOM_LENGTH};
                END
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TRIGGER validate_routine_description_insert
                BEFORE INSERT ON routines
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'routine_description_too_long')
                    WHERE LENGTH(NEW.description) > ${ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH};
                END
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TRIGGER validate_routine_description_update
                BEFORE UPDATE ON routines
                FOR EACH ROW
                BEGIN
                    SELECT RAISE(ABORT, 'routine_description_too_long')
                    WHERE LENGTH(NEW.description) > ${ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH};
                END
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "homeworkmaxxing.db"
        ).fallbackToDestructiveMigration()
            .addCallback(validationCallback)
            .build()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: AppDatabase): RoutineDao = database.routineDao()

    @Provides
    @Singleton
    fun provideCoursDao(database: AppDatabase): CoursDao = database.coursDao()

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao = database.sessionDao()
}
