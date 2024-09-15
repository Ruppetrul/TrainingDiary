import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration2To3 {
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE approach ADD COLUMN confirmed INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE approach SET confirmed = 1")
        }
    }
}