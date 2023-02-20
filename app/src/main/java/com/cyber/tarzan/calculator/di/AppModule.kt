package com.cyber.tarzan.calculator.di

import android.content.Context
import com.cyber.tarzan.calculator.util.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreference {
        return SharedPreference(context)
    }

}