package cn.bakamc.folia.util

import java.util.logging.Level

class Logger(
    private val log: java.util.logging.Logger
) {

    fun info(message: String, vararg params: Any) {
        log.log(Level.INFO, message, params)
    }

    fun debug(message: String,vararg params: Any){
        log.log(Level.WARNING,message,params)
    }

    fun error(message: String,throwable: Throwable){
        log.log(Level.SEVERE,message,throwable)
    }


}