package cn.bakamc.folia.command

import cn.bakamc.folia.BakaMCPlugin

fun registerCommand(){
    BakaMCPlugin.apply {
        getCommand(FlyCommand.CMD)?.setExecutor(FlyCommand)


    }
}