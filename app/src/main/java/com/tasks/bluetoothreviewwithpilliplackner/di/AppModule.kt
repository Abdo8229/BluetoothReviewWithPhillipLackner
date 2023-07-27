package com.tasks.bluetoothreviewwithpilliplackner.di

import android.content.Context
import com.tasks.bluetoothreviewwithpilliplackner.data.AndroidBluetoothController
import com.tasks.bluetoothreviewwithpilliplackner.domain.chat.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
}