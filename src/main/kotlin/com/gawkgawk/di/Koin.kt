package com.gawkgawk.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

/**
 * Install Koin
 */

fun Application.configureDI(){
    install(Koin){
        modules(appModule)
    }
}