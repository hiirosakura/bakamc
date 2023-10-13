import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.initSession

fun main(){

}


fun db(){
    initSession().getMapper(BakaMCPlugin::class.java)



}