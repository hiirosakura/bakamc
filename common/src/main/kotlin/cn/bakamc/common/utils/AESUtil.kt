package cn.bakamc.common.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.utils

 * 文件名 AESUtil

 * 创建时间 2022/11/29 10:41

 * @author forpleuvoir

 */
object AESUtil {

	fun encrypt(plaintext: String, salt: String): String {
		//创建cipher对象
		val cipher = Cipher.getInstance("AES")
		//初始化cipher
		//通过秘钥工厂生产秘钥
		val keySpec = SecretKeySpec(salt.toByteArray(), "AES")
		cipher.init(Cipher.ENCRYPT_MODE, keySpec)
		//加密、解密
		val encrypt = cipher.doFinal(plaintext.toByteArray())
		return String(Base64.getEncoder().encode(encrypt))
	}

	fun decrypt(ciphertext: String, salt: String): String {
		//创建cipher对象
		val cipher = Cipher.getInstance("AES")
		//初始化cipher
		//通过秘钥工厂生产秘钥
		val keySpec = SecretKeySpec(salt.toByteArray(), "AES")
		cipher.init(Cipher.DECRYPT_MODE, keySpec)
		//加密、解密
		val encrypt = cipher.doFinal(Base64.getDecoder().decode(ciphertext))
		return String(encrypt)
	}


}