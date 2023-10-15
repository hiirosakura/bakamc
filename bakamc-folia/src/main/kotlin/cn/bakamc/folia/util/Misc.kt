package cn.bakamc.folia.util

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.session
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun <reified T : BaseMapper<*>> mapper(action: T.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    session {
        getMapper(T::class.java).action()
    }
}


val logger by lazy { BakaMCPlugin.logger }

fun timerTask(run: () -> Unit): TimerTask {
    return object : TimerTask() {
        override fun run() {
            run()
        }
    }
}

fun Timer.schedule(task: SimpleTimerTask) {
    this.schedule(task.task, task.delay, task.period)
}

class SimpleTimerTask(
    val delay: Long,
    val period: Long,
    task: () -> Unit
) {
    val task = timerTask(task)
}