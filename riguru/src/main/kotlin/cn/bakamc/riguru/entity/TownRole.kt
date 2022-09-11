package cn.bakamc.riguru.entity

import com.baomidou.mybatisplus.annotation.IEnum

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.entity

 * 文件名 TownRole

 * 创建时间 2022/9/12 1:33

 * @author forpleuvoir

 */
enum class TownRole(
	val code: Int,
	val description: String
) : IEnum<Int> {
	Major(1, "major"),
	Admin(2, "admin"),
	Member(3, "member");

	override fun getValue(): Int = code

	companion object {
		fun ofCode(code: Int): TownRole {
			return values().first { code == it.code }
		}
	}
}