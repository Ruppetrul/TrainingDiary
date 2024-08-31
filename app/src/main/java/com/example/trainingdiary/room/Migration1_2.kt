import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE BodyPart ADD COLUMN logo TEXT")

            db.execSQL("UPDATE BodyPart SET logo = 'hands_logo' WHERE id = 1")
            db.execSQL("UPDATE BodyPart SET logo = 'legs_logo' WHERE id = 2")
            db.execSQL("UPDATE BodyPart SET logo = 'body_logo' WHERE id = 3")
            db.execSQL("UPDATE BodyPart SET logo = 'back_logo' WHERE id = 4")
            db.execSQL("UPDATE BodyPart SET logo = 'chest_logo' WHERE id = 5")
            db.execSQL("UPDATE BodyPart SET logo = 'shoulders_logo' WHERE id = 6")
            db.execSQL("UPDATE BodyPart SET logo = 'press_logo' WHERE id = 7")
        }
    }
}