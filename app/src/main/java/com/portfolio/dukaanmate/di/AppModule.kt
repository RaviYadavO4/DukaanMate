package com.portfolio.dukaanmate.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.portfolio.dukaanmate.data.local.DukaanDatabase
import com.portfolio.dukaanmate.data.local.LanguagePreferences
import com.portfolio.dukaanmate.data.repository.DukaanRepository
import com.portfolio.dukaanmate.data.repository.DukaanRepositoryImpl
import com.portfolio.dukaanmate.ui.viewmodel.DukaanViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // 1. Language Preferences (Persistent Storage)
    single { LanguagePreferences(androidContext()) }

    // 2. Room Database Singleton with Pre-populated Sample Data
    single {
        Room.databaseBuilder(
            androidContext(),
            DukaanDatabase::class.java,
            "dukaan_mate_db"
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8901262150051', 'Amul Taaza Milk 1L', 68.0, 60.0, 24, 5)")
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8906007280014', 'Fortune Refined Oil 1L', 145.0, 130.0, 15, 5)")
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8901058000155', 'Tata Salt 1kg', 28.0, 22.0, 3, 5)")
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8901030616145', 'Parle-G Gold 100g', 10.0, 8.5, 40, 10)")
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8901058852334', 'Maggi Noodles 70g', 14.0, 12.0, 2, 8)")
                db.execSQL("INSERT INTO products (barcode, name, sellPrice, buyPrice, stockQty, minStockAlert) VALUES ('8901396300012', 'Dettol Soap 125g', 45.0, 38.0, 18, 5)")

                val now = System.currentTimeMillis()
                db.execSQL("INSERT INTO invoices (invoiceNumber, customerName, customerPhone, totalAmount, createdAt) VALUES ('INV-104821', 'Ramesh Kumar', '9876543210', 256.0, $now)")
            }
        })
        .build()
    }

    // 3. DAOs
    single { get<DukaanDatabase>().productDao() }
    single { get<DukaanDatabase>().invoiceDao() }

    // 4. Repository
    single<DukaanRepository> {
        DukaanRepositoryImpl(
            database = get(),
            productDao = get(),
            invoiceDao = get()
        )
    }

    // 5. ViewModel (Repository + LanguagePreferences injected)
    viewModel {
        DukaanViewModel(
            repository = get(),
            languagePreferences = get()
        )
    }
}
