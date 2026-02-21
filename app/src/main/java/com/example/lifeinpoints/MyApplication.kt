package com.example.lifeinpoints

import android.app.Application
import com.example.lifeinpoints.data.category.CategoryDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject lateinit var db: CategoryDatabase

//    override fun onCreate() {
//        super.onCreate()
//        CoroutineScope(Dispatchers.IO).launch {
//            db.clearAllTables()
//        }
//    }
}