package cn.bakamc.folia.database.table

import org.bukkit.entity.Player
import slick.jdbc.MySQLProfile.api.*

import java.util.UUID
import scala.language.implicitConversions

object PlayerInfo {

  implicit def playerToInfo(player: Player): PlayerInfo = PlayerInfo(player)

  implicit def uuidToString(uuid: UUID): String = uuid.toString

  def apply(player: Player): PlayerInfo = {
    PlayerInfo(
      player.getUniqueId,
      player.getName
    )
  }

}

case class PlayerInfo(
  uuid: String,
  name: String,
)

object PlayerInfoImpl {
  given Conversion[Player, PlayerInfo] = PlayerInfo(_)
}


class PlayerInfos(tag: Tag) extends Table[PlayerInfo](tag, "player_info") {
  def uuid = column[String]("uuid", O.PrimaryKey, O.Length(36))

  def name = column[String]("name", O.Length(16))

  def * = (uuid, name).mapTo[PlayerInfo]
}

val playerInfos = TableQuery[PlayerInfos]
